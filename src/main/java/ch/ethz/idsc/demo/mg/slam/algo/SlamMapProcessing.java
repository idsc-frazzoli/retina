// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** extracts way points from a map using threshold operation,
 * morphological processing and connected component labeling */
class SlamMapProcessing implements Runnable {
  private final MapProvider thresholdMap;
  private final Mat dilateKernel = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(8, 8));
  private final Mat erodeKernel = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(3, 3));
  private final double wayPointUpdateRate;
  private final double mapThreshold;
  private final double cornerX;
  private final double cornerY;
  private final double cellDim;
  private final Thread thread = new Thread(this);
  // ---
  private List<double[]> worldWayPoints = new ArrayList<>(); // world frame
  private double lastComputationTimeStamp;
  private MapProvider occurrenceMap;
  private Mat labels;
  private boolean isLaunched = true;

  SlamMapProcessing(SlamConfig slamConfig) {
    thresholdMap = new MapProvider(slamConfig);
    labels = new Mat(thresholdMap.getWidth(), thresholdMap.getHeight(), opencv_core.CV_8U);
    wayPointUpdateRate = Magnitude.SECOND.toDouble(slamConfig._wayPointUpdateRate);
    mapThreshold = slamConfig.mapThreshold.number().doubleValue();
    cornerX = Magnitude.METER.toDouble(slamConfig._corner.Get(0));
    cornerY = Magnitude.METER.toDouble(slamConfig._corner.Get(1));
    cellDim = Magnitude.METER.toDouble(slamConfig._cellDim);
  }

  public void initialize(double initTimeStamp) {
    lastComputationTimeStamp = initTimeStamp;
    thread.start();
  }

  public void stop() {
    // TODO need to cleanly stop operations
    isLaunched = false;
    thread.interrupt();
  }

  /** suggested API:
   * the call to the function "mapPostProcessing" shall be non-blocking.
   * data is passed to the SlamMapProcessing thread if taken into account
   * unless the thread is too busy to process the data. */
  // TODO JPH use timer, but also take case that offline processing is possible
  public void mapPostProcessing(MapProvider occurrenceMap, double currentTimeStamp) {
    if (currentTimeStamp - lastComputationTimeStamp > wayPointUpdateRate) {
      this.occurrenceMap = occurrenceMap;
      thread.interrupt();
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  public Mat getProcessedMat() {
    labels.convertTo(labels, opencv_core.CV_8UC1);
    return labels;
  }

  public List<double[]> getWorldWayPoints() {
    return worldWayPoints;
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched)
      if (Objects.nonNull(occurrenceMap)) {
        SlamMapProcessingUtil.computeThresholdMap(occurrenceMap, mapThreshold, thresholdMap);
        worldWayPoints = SlamMapProcessingUtil.findWayPoints(thresholdMap, labels, dilateKernel, erodeKernel, cornerX, cornerY, cellDim);
        occurrenceMap = null;
      } else
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // ---
        }
  }
}
