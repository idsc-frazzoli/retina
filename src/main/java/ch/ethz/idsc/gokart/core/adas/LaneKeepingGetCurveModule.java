// code by am (used PIDModule by mp as model)
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurveSe2PursuitLcmClient;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ref.TensorListener;

// TODO AM implementation is not complete
public class LaneKeepingGetCurveModule extends AbstractModule implements TensorListener {
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  private final LaneKeepingCenterlineModule laneKeepingCenterlineModule = new LaneKeepingCenterlineModule();

  @Override // from AbstractModule
  protected void first() {
    curveSe2PursuitLcmClient.addListener(this);
    curveSe2PursuitLcmClient.startSubscriptions();
    laneKeepingCenterlineModule.launch();
  }

  @Override // from AbstractModule
  protected void last() {
    curveSe2PursuitLcmClient.stopSubscriptions();
  }

  @Override // from TensorListener
  public void tensorReceived(Tensor tensor) {
    laneKeepingCenterlineModule.setCurve(Optional.of(tensor));
  }
}