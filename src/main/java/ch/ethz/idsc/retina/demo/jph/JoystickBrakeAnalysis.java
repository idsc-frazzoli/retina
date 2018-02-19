// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TableBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

class JoystickBrakeAnalysis implements OfflineTableSupplier {
  private static final String JOYSTICK = "joystick.generic_xbox_pad";
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(JOYSTICK)) {
      JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
      GokartJoystickInterface gji = (GokartJoystickInterface) joystickEvent;
      Scalar scalar = gji.getBreakStrength();
      if (Scalars.nonZero(scalar))
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND).map(Round._2), //
            scalar);
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    File dir = new File("/media/datahaki/backup/gokartlogs/20171213");
    for (File file : dir.listFiles()) {
      String name = file.getName().substring(0, 15);
      System.out.println(name);
      OfflineProcessing.single(file, new JoystickBrakeAnalysis(), name);
    }
  }
}
