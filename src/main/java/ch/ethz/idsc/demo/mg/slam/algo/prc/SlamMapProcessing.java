// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.PeriodicSlamStep;
import ch.ethz.idsc.demo.mg.slam.config.SlamConfig;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.Tensor;

/** extracts way points from a map using threshold operation,
 * morphological processing and connected component labeling */
public class SlamMapProcessing extends PeriodicSlamStep implements Runnable, StartAndStoppable {
  private final Thread thread = new Thread(this);
  private final SlamWaypointDetection slamWaypointDetection;
  private final SlamCurveProcessingHandler handler;
  // ---
  private MapProvider occurrenceMap;
  private boolean isLaunched;

  public SlamMapProcessing(SlamContainer slamContainer, SlamCurveContainer slamCurveContainer) {
    super(slamContainer, SlamConfig.GLOBAL.waypointUpdateRate);
    slamWaypointDetection = new SlamWaypointDetection();
    handler = new SlamCurveProcessingHandler(slamCurveContainer);
    start();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    occurrenceMap = slamContainer.getOccurrenceMap();
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
    slamContainer.setMat(slamWaypointDetection.getProcessedMat());
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
