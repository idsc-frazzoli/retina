// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.lcm.LcmClientAdapter;

public class JoystickLcmClient extends LcmClientAdapter {
  private static final int TIMEOUT_MS = 200;
  // ---
  private final String pattern;

  /** @param pattern for instance "generic_xbox_pad" */
  public JoystickLcmClient(String pattern) {
    this.pattern = pattern;
  }

  private long timeStamp = 0;
  private JoystickEvent joystickEvent = null;

  public Optional<JoystickEvent> getJoystick() {
    return Optional.ofNullable(now() < timeStamp + TIMEOUT_MS ? joystickEvent : null);
  }

  @Override // from LcmClientAdapter
  protected String channel() {
    return "joystick." + pattern;
  }

  @Override // from LcmClientAdapter
  protected void messageReceived(ByteBuffer byteBuffer) {
    joystickEvent = JoystickDecoder.decode(byteBuffer);
    timeStamp = now();
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
