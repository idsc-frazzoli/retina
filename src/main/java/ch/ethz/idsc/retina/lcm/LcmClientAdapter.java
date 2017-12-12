// code by jph
package ch.ethz.idsc.retina.lcm;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;
import lcm.lcm.SubscriptionRecord;

public abstract class LcmClientAdapter implements LcmClientInterface, LCMSubscriber {
  private SubscriptionRecord subscriptionRecord;

  @Override // from LcmClientInterface
  public final void startSubscriptions() { // TODO prevent multiple subscriptions
    subscriptionRecord = LCM.getSingleton().subscribe(channel(), this);
  }

  @Override // from LcmClientInterface
  public final void stopSubscriptions() {
    if (Objects.nonNull(subscriptionRecord))
      LCM.getSingleton().unsubscribe(subscriptionRecord);
  }

  @Override // from LCMSubscriber
  public final void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
    try {
      BinaryBlob binaryBlob = new BinaryBlob(ins); // <- may throw IOException
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      messageReceived(byteBuffer);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  /** @return lcm channel name */
  protected abstract String channel();

  /** callback function invoked as soon as lcm message is received
   * 
   * @param byteBuffer */
  protected abstract void messageReceived(ByteBuffer byteBuffer);
}
