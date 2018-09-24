// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.core.PeriodicSlamStep;
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
    super(slamContainer, SlamCoreConfig.GLOBAL.waypointUpdateRate);
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
        // TODO JAN check thread safety
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
