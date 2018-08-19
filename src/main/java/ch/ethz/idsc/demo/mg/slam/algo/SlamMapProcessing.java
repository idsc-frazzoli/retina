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
/* package */ class SlamMapProcessing implements Runnable {
  private final Mat dilateKernel = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(8, 8));
  private final Mat erodeKernel = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(3, 3));
  private final boolean onlineMode;
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
  private boolean isLaunched;

  SlamMapProcessing(SlamConfig slamConfig) {
    onlineMode = slamConfig.onlineMode;
    wayPointUpdateRate = Magnitude.SECOND.toDouble(slamConfig.wayPointUpdateRate);
    mapThreshold = slamConfig.mapThreshold.number().doubleValue();
    cornerX = Magnitude.METER.toDouble(slamConfig.corner.Get(0));
    cornerY = Magnitude.METER.toDouble(slamConfig.corner.Get(1));
    cellDim = Magnitude.METER.toDouble(slamConfig.cellDim);
    labels = new Mat(slamConfig.mapWidth(), slamConfig.mapHeight(), opencv_core.CV_8U);
  }

  public void initialize(double initTimeStamp) {
    lastComputationTimeStamp = initTimeStamp;
    isLaunched = true;
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
  public void mapPostProcessing(MapProvider occurrenceMap, double currentTimeStamp) {
    if (!onlineMode && (currentTimeStamp - lastComputationTimeStamp > wayPointUpdateRate)) {
      this.occurrenceMap = occurrenceMap;
      thread.interrupt();
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  // to be called by timerTask
  public void mapPostProcessing(MapProvider occurrenceMap) {
    this.occurrenceMap = occurrenceMap;
    thread.interrupt();
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
        worldWayPoints = SlamMapProcessingUtil.findWayPoints(occurrenceMap, labels, dilateKernel, erodeKernel, mapThreshold, cornerX, cornerY, cellDim);
        occurrenceMap = null;
      } else
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // ---
        }
  }
}
