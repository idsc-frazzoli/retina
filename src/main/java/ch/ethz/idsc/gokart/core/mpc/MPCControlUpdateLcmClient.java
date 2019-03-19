// code by jph, mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

public class MPCControlUpdateLcmClient extends SimpleLcmClient<MPCControlUpdateInterface> {
  public MPCControlUpdateLcmClient() {
    super(GokartLcmChannel.MPC_FORCES_CNS);
  }

  @Override // from SimpleLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    ControlAndPredictionStepsMessage controlAndPredictionStepsMessage = new ControlAndPredictionStepsMessage(byteBuffer);
    for (MPCControlUpdateInterface listener : listeners)
      listener.getControlAndPredictionSteps(controlAndPredictionStepsMessage.controlAndPredictionSteps);
  }
}
