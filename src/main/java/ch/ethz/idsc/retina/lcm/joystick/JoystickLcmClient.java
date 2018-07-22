// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;

/** client to lcm channel with joystick information
 * 
 * JoystickLcmClient is useful for offline processing
 * 
 * {@link JoystickLcmProvider} is suitable for modules that control live operations */
public class JoystickLcmClient extends BinaryLcmClient {
  private final String channel;
  private final List<JoystickListener> listeners = new LinkedList<>();

  /** @param channel for instance "joystick.generic_xbox_pad" */
  public JoystickLcmClient(String channel) {
    this.channel = channel;
  }

  @Override // from LcmClientAdapter
  protected String channel() {
    return channel;
  }

  @Override // from LcmClientAdapter
  protected final void messageReceived(ByteBuffer byteBuffer) {
    JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
    listeners.forEach(listener -> listener.joystick(joystickEvent));
  }

  public void addListener(JoystickListener joystickListener) {
    listeners.add(joystickListener);
  }

  public void removeListener(JoystickListener joystickListener) {
    listeners.remove(joystickListener);
  }
}
