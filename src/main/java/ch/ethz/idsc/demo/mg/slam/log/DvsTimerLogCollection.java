// code by mg
package ch.ethz.idsc.demo.mg.slam.log;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.core.PeriodicSlamStep;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** save CSV logs with timestamps provided by DavisDvsEvent stream. This is suitable for online log processing
 * when dvs timestamps represent "true" time, or for offline log processing when the DVS timestamps represent the time at which
 * the events were captured */
public class DvsTimerLogCollection extends PeriodicSlamStep implements StartAndStoppable {
  private final SlamLogSave slamLogSave;

  public DvsTimerLogCollection(SlamCoreContainer slamCoreContainer, SlamPrcContainer slamPrcContainer, //
      GokartPoseInterface gokartPoseInterface, SlamEventCounter slamEventCounter) {
    super(slamCoreContainer, SlamCoreConfig.GLOBAL.logCollectionUpdateRate);
    slamLogSave = new SlamLogSave(slamCoreContainer, slamPrcContainer, gokartPoseInterface, slamEventCounter);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    slamLogSave.logSaveTask(currentTimeStamp);
  }

  @Override // from StartAndStoppable
  public void start() {
    // ---
  }

  @Override // from StartAndStoppable
  public void stop() {
    slamLogSave.stop();
  }
}
