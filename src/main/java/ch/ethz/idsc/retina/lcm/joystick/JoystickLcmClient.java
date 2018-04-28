// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.lcm.autobox.BinaryLcmClient;
import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;

/** client to lcm channel with joystick information */
public class JoystickLcmClient extends BinaryLcmClient {
  private final String pattern;
  private final List<JoystickListener> listeners = new LinkedList<>();

  /** @param pattern for instance "generic_xbox_pad" */
  public JoystickLcmClient(String pattern) {
    this.pattern = pattern;
  }

  @Override // from LcmClientAdapter
  protected String channel() {
    return "joystick." + pattern;
  }

  @Override // from LcmClientAdapter
  protected final void messageReceived(ByteBuffer byteBuffer) {
    JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
    listeners.forEach(listener -> listener.joystick(joystickEvent));
  }

  public void addListener(JoystickListener joystickListener) {
    listeners.add(joystickListener);
  }
}
