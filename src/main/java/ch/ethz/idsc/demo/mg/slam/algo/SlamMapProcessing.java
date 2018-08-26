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
  private final double waypointUpdateRate; // [s]
  private final SlamMapProcessingUtil slamMapProcessingUtil;
  // TODO MG can labels be moved to SlamMapProcessingUtil?
  private final Mat labels;
  // ---
  private MapProvider occurrenceMap;
  private boolean isLaunched;
  private double lastComputationTimeStamp;

  public SlamMapProcessing(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    waypointUpdateRate = Magnitude.SECOND.toDouble(slamConfig.waypointUpdateRate);
    labels = new Mat(slamConfig.mapWidth(), slamConfig.mapHeight(), opencv_core.CV_8U);
    slamMapProcessingUtil = new SlamMapProcessingUtil(slamConfig);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!isLaunched) { // TODO MG launch thread elsewhere, talk to jan
      isLaunched = true;
      thread.start();
    }
    // TODO use int for checking
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    if (currentTimeStamp - lastComputationTimeStamp > waypointUpdateRate) {
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
    List<double[]> worldWaypoints = slamMapProcessingUtil.findWaypoints(occurrenceMap, labels);
    slamContainer.setWaypoints(slamMapProcessingUtil.getWaypoints( //
        worldWaypoints, slamContainer.getSlamEstimatedPose().getPoseUnitless()));
  }

  // TODO MG currently unused, if planned to used in the future, create comment for function
  public Mat getProcessedMat() {
    labels.convertTo(labels, opencv_core.CV_8UC1);
    return labels;
  }
}
