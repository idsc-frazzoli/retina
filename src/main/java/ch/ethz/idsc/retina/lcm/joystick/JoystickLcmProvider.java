// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.lcm.autobox.BinaryLcmClient;
import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;

/** client to lcm channel with joystick information */
public final class JoystickLcmProvider extends BinaryLcmClient {
  private final String pattern;
  private final int timeout_ms;
  // ---
  private long timeStamp = 0;
  private JoystickEvent joystickEvent = null;

  /** @param pattern for instance "generic_xbox_pad"
   * @param timeout_ms maximum age of joystick information relayed to application layer */
  public JoystickLcmProvider(String pattern, int timeout_ms) {
    this.pattern = pattern;
    this.timeout_ms = timeout_ms;
  }

  /** @return recent joystick readout, or empty */
  public Optional<JoystickEvent> getJoystick() {
    return Optional.ofNullable(now() < timeStamp + timeout_ms ? joystickEvent : null);
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

  // helper function
  private static long now() {
    return System.currentTimeMillis();
  }
}
