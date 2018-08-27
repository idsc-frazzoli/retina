// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** extracts way points from a map using threshold operation,
 * morphological processing and connected component labeling */
/* package */ class SlamMapProcessing extends AbstractSlamStep implements Runnable {
  private final Thread thread = new Thread(this);
  private final int waypointUpdateRate; // [us]
  private final SlamMapProcessingUtil slamMapProcessingUtil;
  // ---
  private MapProvider occurrenceMap;
  private boolean isLaunched;
  private Integer lastComputationTimeStamp = null;

  public SlamMapProcessing(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    waypointUpdateRate = Magnitude.MICRO_SECOND.toInt(slamConfig.waypointUpdateRate);
    slamMapProcessingUtil = new SlamMapProcessingUtil(slamConfig);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!isLaunched) { // TODO MG launch thread elsewhere, talk to jan
      isLaunched = true;
      thread.start();
    }
    initializeTimeStamps(davisDvsEvent.time);
    if (davisDvsEvent.time - lastComputationTimeStamp > waypointUpdateRate) {
      occurrenceMap = slamContainer.getOccurrenceMap();
      thread.interrupt();
      lastComputationTimeStamp = davisDvsEvent.time;
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

  private void initializeTimeStamps(int initTimeStamp) {
    if (Objects.isNull(lastComputationTimeStamp))
      lastComputationTimeStamp = initTimeStamp;
  }

  private void mapProcessing() {
    List<double[]> worldWaypoints = slamMapProcessingUtil.findWaypoints(occurrenceMap);
    slamContainer.setWaypoints(slamMapProcessingUtil.getWaypoints( //
        worldWaypoints, slamContainer.getPoseUnitless()));
  }
}
