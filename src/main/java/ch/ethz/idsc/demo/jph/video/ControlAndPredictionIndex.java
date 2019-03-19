// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionStepsMessage;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;

/* package */ class ControlAndPredictionIndex implements OfflineLogListener {
  public static NavigableMap<Scalar, ControlAndPredictionSteps> build(File file) throws IOException {
    ControlAndPredictionIndex controlAndPredictionIndex = new ControlAndPredictionIndex();
    OfflineLogPlayer.process(file, controlAndPredictionIndex);
    return controlAndPredictionIndex.navigableMap;
  }

  // ---
  private final NavigableMap<Scalar, ControlAndPredictionSteps> navigableMap = new TreeMap<>();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.MPC_FORCES_CNS)) {
      ControlAndPredictionStepsMessage controlAndPredictionStepsMessage = //
          new ControlAndPredictionStepsMessage(byteBuffer);
      navigableMap.put(time, controlAndPredictionStepsMessage.controlAndPredictionSteps);
    }
  }
}
