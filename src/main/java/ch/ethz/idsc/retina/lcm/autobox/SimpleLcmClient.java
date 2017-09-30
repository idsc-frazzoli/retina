// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public abstract class SimpleLcmClient<L> implements LcmClientInterface, LCMSubscriber {
  private final String channel;
  protected final List<L> listeners = new CopyOnWriteArrayList<>();

  public SimpleLcmClient(String channel) {
    this.channel = channel;
  }

  public final void addListener(L listener) {
    listeners.add(listener);
  }

  @Override
  public final void startSubscriptions() {
    LCM.getSingleton().subscribe(channel, this);
  }

  @Override
  public final void stopSubscriptions() {
    LCM.getSingleton().unsubscribe(channel, this);
  }

  @Override
  public final void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
    try {
      BinaryBlob binaryBlob = new BinaryBlob(ins);
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      createEvent(byteBuffer);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  protected abstract void createEvent(ByteBuffer byteBuffer);
}
