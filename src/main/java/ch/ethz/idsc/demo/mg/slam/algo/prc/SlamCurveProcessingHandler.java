// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;

import ch.ethz.idsc.tensor.Tensor;

// handles the curve processing
public class SlamCurveProcessingHandler {
  private final SlamCurveContainer slamCurveContainer;
  private final List<CurveListener> listeners;

  public SlamCurveProcessingHandler(SlamCurveContainer slamCurveContainer) {
    this.slamCurveContainer = slamCurveContainer;
    listeners = SlamCurveProcessingUtil.getListeners(slamCurveContainer);
  }

  public void invoke(Tensor worldWaypoints) {
    slamCurveContainer.setWorldWaypoints(worldWaypoints);
    listeners.forEach(listener -> listener.process());
  }
}
