// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionStepsMessage;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Flatten;

public enum MpcControlPredictionChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override
  public String channel() {
    return GokartLcmChannel.MPC_FORCES_CNS;
  }

  @Override
  public Tensor row(ByteBuffer byteBuffer) {
    ControlAndPredictionSteps controlAndPredictionSteps = //
        new ControlAndPredictionStepsMessage(byteBuffer).getPayload();
    return Flatten.of(controlAndPredictionSteps.asMatrix());
  }
}
