// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.SlamMapProcessingUtil;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** extracts way points from a map using threshold operation,
 * morphological processing and connected component labeling */
/* package */ class SlamMapProcessingListener implements DavisDvsListener, Runnable {
  private final Mat dilateKernel = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(8, 8));
  private final Mat erodeKernel = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(3, 3));
  private final SlamContainer slamContainer;
  private final Thread thread = new Thread(this);
  private final double wayPointUpdateRate;
  private final double mapThreshold;
  private final double cornerX;
  private final double cornerY;
  private final double cellDim;
  // ---
  private List<double[]> worldWayPoints = new ArrayList<>(); // world frame
  private MapProvider occurrenceMap;
  private Mat labels;
  private boolean isLaunched;
  private double lastComputationTimeStamp;

  public SlamMapProcessingListener(SlamConfig slamConfig, SlamContainer slamContainer) {
    this.slamContainer = slamContainer;
    wayPointUpdateRate = Magnitude.SECOND.toDouble(slamConfig.wayPointUpdateRate);
    mapThreshold = slamConfig.mapThreshold.number().doubleValue();
    cornerX = Magnitude.METER.toDouble(slamConfig.corner.Get(0));
    cornerY = Magnitude.METER.toDouble(slamConfig.corner.Get(1));
    cellDim = Magnitude.METER.toDouble(slamConfig.cellDim);
    labels = new Mat(slamConfig.mapWidth(), slamConfig.mapHeight(), opencv_core.CV_8U);
  }

  public void start() {
    isLaunched = true;
    thread.start();
  }

  public void stop() {
    // TODO need to cleanly stop operations
    isLaunched = false;
    thread.interrupt();
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    if (currentTimeStamp - lastComputationTimeStamp > wayPointUpdateRate) {
      occurrenceMap = slamContainer.getOccurrenceMap();
      thread.interrupt();
      lastComputationTimeStamp = currentTimeStamp;
    }
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

  public Mat getProcessedMat() {
    labels.convertTo(labels, opencv_core.CV_8UC1);
    return labels;
  }

  public List<double[]> getWorldWayPoints() {
    return worldWayPoints;
  }
}
