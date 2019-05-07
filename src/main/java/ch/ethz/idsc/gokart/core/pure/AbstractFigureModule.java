// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ref.TensorListener;

/** listens to curves for pursuit */
public abstract class AbstractFigureModule extends AbstractModule implements TensorListener {
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  private final CurvePursuitModule curvePursuitModule;

  protected AbstractFigureModule(CurvePursuitModule curvePursuitModule) {
    this.curvePursuitModule = curvePursuitModule;
  }

  @Override // from AbstractModule
  protected final void first() {
    curvePursuitModule.launch();
    curveSe2PursuitLcmClient.addListener(this);
    curveSe2PursuitLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected final void last() {
    curveSe2PursuitLcmClient.stopSubscriptions();
    curvePursuitModule.terminate();
  }

  @Override // from TensorListener
  public void tensorReceived(Tensor tensor) {
    curvePursuitModule.setCurve(Optional.of(tensor));
  }
}
