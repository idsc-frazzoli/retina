// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public class JoystickLcmClient implements LcmClientInterface, LCMSubscriber {
  private final JoystickType joystickType;
  private final List<JoystickListener> listeners = new LinkedList<>();

  public JoystickLcmClient(JoystickType joystickType) {
    this.joystickType = joystickType;
  }

  public void addListener(JoystickListener joystickEventListener) {
    listeners.add(joystickEventListener);
  }

  @Override
  public void startSubscriptions() {
    LCM.getSingleton().subscribe("joystick." + joystickType.name().toLowerCase(), this);
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
