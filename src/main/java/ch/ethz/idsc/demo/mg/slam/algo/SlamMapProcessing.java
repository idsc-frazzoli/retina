// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;
import java.util.Objects;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** extracts way points from a map using threshold operation,
 * morphological processing and connected component labeling */
/* package */ class SlamMapProcessing extends AbstractSlamStep implements Runnable {
  private final Thread thread = new Thread(this);
  private final double wayPointUpdateRate;
  private final double mapThreshold;
  private final double cornerX;
  private final double cornerY;
  private final double cellDim;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;
  // ---
  private MapProvider occurrenceMap;
  private Mat labels;
  private boolean isLaunched;
  private double lastComputationTimeStamp;

  public SlamMapProcessing(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    wayPointUpdateRate = Magnitude.SECOND.toDouble(slamConfig.wayPointUpdateRate);
    mapThreshold = slamConfig.mapThreshold.number().doubleValue();
    cornerX = Magnitude.METER.toDouble(slamConfig.corner.Get(0));
    cornerY = Magnitude.METER.toDouble(slamConfig.corner.Get(1));
    cellDim = Magnitude.METER.toDouble(slamConfig.cellDim);
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = (visibleBoxXMax - visibleBoxXMin) * 0.5;
    labels = new Mat(slamConfig.mapWidth(), slamConfig.mapHeight(), opencv_core.CV_8U);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!isLaunched) {
      isLaunched = true;
      thread.start();
    }
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
        mapProcessing();
        occurrenceMap = null;
      } else
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // ---
        }
  }

  private void mapProcessing() {
    List<double[]> worldWayPoints = SlamMapProcessingUtil.findWayPoints(occurrenceMap, labels, mapThreshold, cornerX, cornerY, cellDim);
    slamContainer.setWayPoints(SlamMapProcessingUtil.getWayPoints(worldWayPoints, slamContainer.getSlamEstimatedPose().getPoseUnitless(), visibleBoxXMin,
        visibleBoxXMax, visibleBoxHalfWidth));
  }

  // currently unused
  public Mat getProcessedMat() {
    labels.convertTo(labels, opencv_core.CV_8UC1);
    return labels;
  }
}
