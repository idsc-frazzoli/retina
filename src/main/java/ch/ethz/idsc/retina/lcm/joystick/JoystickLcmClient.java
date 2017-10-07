// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public class JoystickLcmClient implements LcmClientInterface, LCMSubscriber {
  /** @return */
  public static JoystickLcmClient any() {
    return new JoystickLcmClient("*");
  }

  // ---
  private final List<JoystickListener> listeners = new CopyOnWriteArrayList<>();
  private final String pattern;
  private final Set<String> set = new HashSet<>();

  /** @param pattern for instance "generic_xbox_pad" or "*" for all */
  public JoystickLcmClient(String pattern) {
    this.pattern = pattern;
  }

  public void addListener(JoystickListener joystickListener) {
    listeners.add(joystickListener);
  }

  public void removeListener(JoystickListener joystickListener) {
    boolean removed = listeners.remove(joystickListener);
    if (!removed)
      System.err.println("joystick not found.");
  }

  @Override
  public void startSubscriptions() {
    LCM.getSingleton().subscribe(_name(), this);
  }

  @Override
  public void stopSubscriptions() {
    if (!listeners.isEmpty())
      System.err.println("warning: listeners still precent, yet unsubscribe");
    LCM.getSingleton().unsubscribe(_name(), this);
  }

  private String _name() {
    return "joystick." + pattern;
  }

  @Override
  public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
    set.add(channel);
    if (set.size() == 1)
      try {
        BinaryBlob binaryBlob = new BinaryBlob(ins); // <- may throw IOException
        ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
        listeners.forEach(listener -> listener.joystick(joystickEvent));
      } catch (IOException exception) {
        exception.printStackTrace();
      }
  }
}
