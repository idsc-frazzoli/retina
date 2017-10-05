// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public enum JoystickLcmClient implements LcmClientInterface, LCMSubscriber {
  INSTANCE;
  // ---
  private final List<JoystickListener> listeners = new CopyOnWriteArrayList<>();

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
    return "joystick.*";
  }

  @Override
  public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
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
