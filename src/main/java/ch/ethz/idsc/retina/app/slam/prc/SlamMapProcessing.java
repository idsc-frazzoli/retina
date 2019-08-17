// code by mg
package ch.ethz.idsc.retina.app.slam.prc;

import java.util.Objects;

import ch.ethz.idsc.retina.app.slam.MapProvider;
import ch.ethz.idsc.retina.app.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.app.slam.SlamPrcContainer;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.app.slam.core.PeriodicSlamStep;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.Tensor;

/** extracts way points from the occurrence map and invokes all way point processing modules.
 * Runs in separate thread */
public class SlamMapProcessing extends PeriodicSlamStep implements Runnable, StartAndStoppable {
  private final Thread thread = new Thread(this);
  private final SlamWaypointDetection slamWaypointDetection;
  private final SlamPrcHandler handler;
  // ---
  private MapProvider occurrenceMap;
  private boolean isLaunched;

  public SlamMapProcessing(SlamCoreContainer slamContainer, SlamPrcContainer slamPrcContainer) {
    super(slamContainer, SlamDvsConfig.eventCamera.slamCoreConfig.waypointUpdateRate);
    slamWaypointDetection = new SlamWaypointDetection();
    handler = new SlamPrcHandler(slamPrcContainer);
    start();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    occurrenceMap = slamCoreContainer.getOccurrenceMap();
    thread.interrupt();
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched)
      if (Objects.nonNull(occurrenceMap)) {
        // TODO JPH check thread safety
        mapProcessing();
        occurrenceMap = null;
      } else
        try {
          Thread.sleep(1000);
        } catch (Exception exception) {
          // ---
        }
  }

  private void mapProcessing() {
    Tensor worldWaypoints = slamWaypointDetection.detectWaypoints(occurrenceMap);
    slamCoreContainer.setLabels(slamWaypointDetection.getProcessedMat());
    handler.invoke(worldWaypoints);
  }

  @Override // from StartAndStoppable
  public void start() {
    isLaunched = true;
    thread.start();
  }

  @Override // from StartAndStoppable
  public void stop() {
    isLaunched = false;
  }
}
