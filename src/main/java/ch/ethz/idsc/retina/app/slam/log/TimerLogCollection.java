// code by mg
package ch.ethz.idsc.retina.app.slam.log;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.retina.app.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.app.slam.SlamPrcContainer;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseInterface;

/** saving logs with timestamps provided by a timer. Suitable for offline log processing
 * when we are interested in algorithm run time */
public class TimerLogCollection implements StartAndStoppable, DavisDvsListener {
  private final SlamLogSave slamLogSave;
  private final Timer timer = new Timer();
  private final TimerTask logSaveTask = new TimerTask() {
    @Override
    public void run() {
      logSaveTask();
    }
  };
  private final long periodicSavePeriod;
  private final int initialTimestamp;

  public TimerLogCollection(SlamCoreContainer slamCoreContainer, SlamPrcContainer slamPrcContainer, PoseInterface poseInterface,
      SlamEventCounter slamEventCounter) {
    slamLogSave = new SlamLogSave(slamCoreContainer, slamPrcContainer, poseInterface, slamEventCounter);
    periodicSavePeriod = Magnitude.MILLI_SECOND.toLong(SlamDvsConfig.eventCamera.slamCoreConfig.logCollectionUpdateRate);
    timer.scheduleAtFixedRate(logSaveTask, 0, periodicSavePeriod);
    initialTimestamp = (int) System.currentTimeMillis();
  }

  private void logSaveTask() {
    int currentTimestamp = (int) (System.currentTimeMillis() - initialTimestamp) * 1000;
    slamLogSave.logSaveTask(currentTimestamp);
  }

  // we implement the interface such that we can elegantly stop the module
  // through the call stopStoppableListeners() in abstractFilterHandler
  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // ---
  }

  @Override // from StartAndStoppable
  public void start() {
    // ---
  }

  @Override // from StartAndStoppable
  public void stop() {
    slamLogSave.stop();
    timer.cancel();
  }
}
