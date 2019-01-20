// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.dev.u3.GokartLabjackFrame;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;

public enum ManualControlParser {
  ;
  public static Optional<ManualControlInterface> event(String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.LABJACK_U3_ADC))
      return Optional.of(new GokartLabjackFrame(byteBuffer));
    if (channel.equals(GokartLcmChannel.JOYSTICK))
      return Optional.of((ManualControlInterface) JoystickDecoder.decode(byteBuffer));
    return Optional.empty();
  }
}
