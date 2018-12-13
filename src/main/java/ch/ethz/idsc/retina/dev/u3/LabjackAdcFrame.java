// code by jph
package ch.ethz.idsc.retina.dev.u3;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class LabjackAdcFrame extends DataEvent implements Serializable {
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

  @Override
  protected void insert(ByteBuffer byteBuffer) {
    for (int index = 0; index < array.length; ++index)
      byteBuffer.putFloat(array[index]);
  }

  @Override
  public Tensor asVector() {
    return Tensors.vectorFloat(array);
  }

  @Override
  protected int length() {
    return array.length * 4;
  }
}
