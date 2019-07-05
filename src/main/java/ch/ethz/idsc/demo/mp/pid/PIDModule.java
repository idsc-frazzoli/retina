// code by mcp (used CenterLinePursuitModule by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurveSe2PursuitLcmClient;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ref.TensorListener;

/** module requires the TrackReconModule to provide the center line of an
 * identified track */
public class PIDModule extends AbstractModule implements TensorListener {
  private final PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();

  @Override // from AbstractModule
  protected void first() {
    curveSe2PursuitLcmClient.addListener(this);
    curveSe2PursuitLcmClient.startSubscriptions();
    pidControllerModule.launch();
  }

  @Override // from AbstractModule
  protected void last() {
    curveSe2PursuitLcmClient.stopSubscriptions();
    pidControllerModule.terminate();
  }

  @Override // from TensorListener
  public void tensorReceived(Tensor tensor) {
    pidControllerModule.setCurve(Optional.of(tensor));
  }
}