// code by am (used PIDModule by mp as model)
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurveSe2PursuitLcmClient;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ref.TensorListener;

public class LaneKeepingGetCurveModule extends AbstractModule implements TensorListener {
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  private final LaneKeepingCenterlineModule laneKeepingTrajectoryModule = new LaneKeepingCenterlineModule();

  @Override // from AbstractModule
  protected void first() {
    curveSe2PursuitLcmClient.addListener(this);
    curveSe2PursuitLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    curveSe2PursuitLcmClient.stopSubscriptions();
  }

  @Override // from TensorListener
  public void tensorReceived(Tensor tensor) {
    laneKeepingTrajectoryModule.setCurve(Optional.of(tensor));
  }
}