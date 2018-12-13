// code by jph
package ch.ethz.idsc.retina.dev.u3;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;

public class LabjackAdcFrame extends DataEvent implements Serializable {
  private static final Clip CLIP = Clip.function(0.1, 5);
  private final float[] array;

  public LabjackAdcFrame(float[] array) {
    this.array = array;
  }

  public LabjackAdcFrame(ByteBuffer byteBuffer) {
    int n = byteBuffer.remaining() / 4;
    array = new float[n];
    for (int index = 0; index < array.length; ++index)
      array[index] = byteBuffer.getFloat();
  }

  /** @return */
  public Scalar getAhead() {
    return CLIP.rescale(RealScalar.of(array[2]));
  }

  @Override // from DataEvent
  protected void insert(ByteBuffer byteBuffer) {
    for (int index = 0; index < array.length; ++index)
      byteBuffer.putFloat(array[index]);
  }

  @Override // from DataEvent
  protected int length() {
    return array.length * 4;
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vectorFloat(array);
  }
}
