// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;
import lcm.lcm.SubscriptionRecord;

public class JoystickLcmClient implements LcmClientInterface, LCMSubscriber {
  private static final int TIMEOUT_MS = 200;
  // ---
  private final String pattern;
  private SubscriptionRecord subscriptionRecord;

  /** @param pattern for instance "generic_xbox_pad" */
  public JoystickLcmClient(String pattern) {
    this.pattern = pattern;
  }

  @Override // from LcmClientInterface
  public void startSubscriptions() {
    subscriptionRecord = LCM.getSingleton().subscribe("joystick." + pattern, this);
  }

  @Override // from LcmClientInterface
  public void stopSubscriptions() {
    if (Objects.nonNull(subscriptionRecord))
      LCM.getSingleton().unsubscribe(subscriptionRecord);
  }

  private long timeStamp = 0;
  private JoystickEvent joystickEvent = null;

  @Override // from LCMSubscriber
  public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
    try {
      BinaryBlob binaryBlob = new BinaryBlob(ins); // <- may throw IOException
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      joystickEvent = JoystickDecoder.decode(byteBuffer);
      timeStamp = now();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public Optional<JoystickEvent> getJoystick() {
    return Optional.ofNullable(now() < timeStamp + TIMEOUT_MS ? joystickEvent : null);
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
