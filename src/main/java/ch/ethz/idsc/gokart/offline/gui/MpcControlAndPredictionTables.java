package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionStepsMessage;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Export;

/* package */ class MpcControlAndPredictionTables implements OfflineLogListener {
  private final File dest_folder;
  private int count = -1;

  public MpcControlAndPredictionTables(File dest_folder) {
    this.dest_folder = dest_folder;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.MPC_FORCES_CNS)) {
      ControlAndPredictionSteps controlAndPredictionSteps = //
          new ControlAndPredictionStepsMessage(byteBuffer).getPayload();
      try {
        Export.of(new File(dest_folder, String.format("%06d.csv", ++count)), controlAndPredictionSteps.asMatrix());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}
