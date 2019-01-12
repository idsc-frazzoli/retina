// code by jph
package ch.ethz.idsc.retina.u3;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** LabjackAdcFrame holds an arbitrary number of ADC readings
 * 
 * immutable */
public final class LabjackAdcFrame extends DataEvent implements Serializable {
  private final float[] array;

  public LabjackAdcFrame(float[] array) {
    this.array = array;
  }

  public LabjackAdcFrame(ByteBuffer byteBuffer) {
    int n = byteBuffer.remaining() >> 2; // division by 4 == Float.BYTES
    array = new float[n];
    for (int index = 0; index < array.length; ++index)
      array[index] = byteBuffer.getFloat();
  }

  @Override // from DataEvent
  protected void insert(ByteBuffer byteBuffer) {
    for (int index = 0; index < array.length; ++index)
      byteBuffer.putFloat(array[index]);
  }

  @Override // from DataEvent
  protected int length() {
    return array.length << 2; // multiplication by 4 == Float.BYTES
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vectorFloat(array);
  }

  /** @param index
   * @return voltage reading of ADC with given index */
  public float getADC_V(int index) {
    return array[index];
  }
}
