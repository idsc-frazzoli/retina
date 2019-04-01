// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.tensor.Tensor;

// TODO JPH not used yet
/* package */ abstract class ObjectChannelInterface<T extends OfflineVectorInterface> implements SingleChannelInterface {
  public abstract T from(ByteBuffer byteBuffer);

  @Override
  public final Tensor row(ByteBuffer byteBuffer) {
    return from(byteBuffer).asVector();
  }
}
