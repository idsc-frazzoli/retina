// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public abstract class BinaryLcmClient implements LcmClientInterface, LCMSubscriber {
  @Override
  public final void startSubscriptions() {
    LCM.getSingleton().subscribe(name(), this);
  }

  @Override
  public final void stopSubscriptions() {
    LCM.getSingleton().unsubscribe(name(), this);
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
