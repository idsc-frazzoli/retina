// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;
import lcm.lcm.SubscriptionRecord;

public abstract class BinaryLcmClient implements LcmClientInterface, LCMSubscriber {
  private SubscriptionRecord subscriptionRecord = null;

  @Override
  public final void startSubscriptions() {
    if (Objects.isNull(subscriptionRecord))
      subscriptionRecord = LCM.getSingleton().subscribe(name(), this);
    else
      System.err.println("already started subscription");
  }

  @Override
  public final void stopSubscriptions() {
    if (Objects.nonNull(subscriptionRecord))
      LCM.getSingleton().unsubscribe(subscriptionRecord);
  }

  @Override
  public final void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
    try {
      BinaryBlob binaryBlob = new BinaryBlob(ins);
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      digest(byteBuffer);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  protected abstract void digest(ByteBuffer byteBuffer);

  protected abstract String name();
}
