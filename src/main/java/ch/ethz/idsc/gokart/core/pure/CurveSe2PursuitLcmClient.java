// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.gokart.lcm.mod.Se2CurveLcm;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ref.TensorListener;

public class CurveSe2PursuitLcmClient extends SimpleLcmClient<TensorListener> {
  public CurveSe2PursuitLcmClient() {
    super(GokartLcmChannel.PURSUIT_CURVE_SE2);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    Tensor tensor = Se2CurveLcm.decode(byteBuffer).unmodifiable();
    listeners.forEach(tensorListener -> tensorListener.tensorReceived(tensor));
  }
}
