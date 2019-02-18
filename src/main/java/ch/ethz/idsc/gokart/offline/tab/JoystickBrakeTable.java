// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.joystick.JoystickEvent;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

public class JoystickBrakeTable implements OfflineTableSupplier {
  private static final String JOYSTICK = "joystick.generic_xbox_pad";
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(JOYSTICK)) {
      JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
      ManualControlInterface manualControlInterface = (ManualControlInterface) joystickEvent;
      Scalar scalar = manualControlInterface.getBreakStrength();
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
}
