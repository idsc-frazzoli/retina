// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;
import lcm.lcm.SubscriptionRecord;

public abstract class BinaryLcmClient implements LcmClientInterface, LCMSubscriber {
  private final String channel;
  private SubscriptionRecord subscriptionRecord = null;

  public BinaryLcmClient(String channel) {
    this.channel = channel;
  }

  @Override // from LcmClientInterface
  public final void startSubscriptions() {
    if (Objects.isNull(subscriptionRecord))
      subscriptionRecord = LCM.getSingleton().subscribe(channel, this);
    else
      System.err.println("already started subscription");
  }

  @Override // from LcmClientInterface
  public final void stopSubscriptions() {
    if (Objects.nonNull(subscriptionRecord)) {
      LCM.getSingleton().unsubscribe(subscriptionRecord);
      subscriptionRecord = null;
    }
  }

  @Override // from LCMSubscriber
  public final void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
    try {
      BinaryBlob binaryBlob = new BinaryBlob(ins);
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      messageReceived(byteBuffer);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  protected abstract void messageReceived(ByteBuffer byteBuffer);
}
