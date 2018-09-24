// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.tensor.Tensor;

/** handles the way point processing modules */
/* package */ class SlamPrcHandler {
  private final SlamPrcContainer slamPrcContainer;
  private final List<CurveListener> listeners;

  public SlamPrcHandler(SlamPrcContainer slamCurveContainer) {
    this.slamPrcContainer = slamCurveContainer;
    listeners = SlamPrcHandlerUtil.getListeners(slamCurveContainer);
  }

  /** @param worldWaypoints detected way points in world frame */
  public void invoke(Tensor worldWaypoints) {
    slamPrcContainer.setWaypoints(worldWaypoints);
    listeners.forEach(listener -> listener.process());
  }
}
