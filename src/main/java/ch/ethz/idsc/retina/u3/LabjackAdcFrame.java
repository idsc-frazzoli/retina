// code by jph
package ch.ethz.idsc.retina.u3;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** LabjackAdcFrame holds an arbitrary number of ADC readings
 * 
 * immutable
 * 
 * code is tested with device Labjack U3
 * Reference: "U3 Datasheet"
 * https://polybox.ethz.ch/index.php/s/ewooaNkYdmM3EdQ */
public final class LabjackAdcFrame extends DataEvent implements Serializable {
  private final float[] array;

  public LabjackAdcFrame(ByteBuffer byteBuffer) {
    int n = byteBuffer.remaining() >> 2; // division by 4 == Float.BYTES
    array = new float[n];
    for (int index = 0; index < array.length; ++index)
      array[index] = byteBuffer.getFloat();
  }

  /** Hint: use constructor only in tests because the array is mutable
   * 
   * @param array */
  public LabjackAdcFrame(float[] array) {
    this.array = array;
  }

  @Override // from DataEvent
  public void insert(ByteBuffer byteBuffer) {
    for (int index = 0; index < array.length; ++index)
      byteBuffer.putFloat(array[index]);
  }

  @Override // from DataEvent
  public int length() {
    return array.length << 2; // multiplication by 4 == Float.BYTES
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vectorFloat(array);
  }

  /** @param index
   * @return voltage reading of ADC with given index and unit [V] */
  public Scalar getADC(int index) {
    return Quantity.of(array[index], SI.VOLT);
  }

  /** @return {ADC[0], ADC[1], ...} */
  public Tensor allADC() {
    return Tensors.vector(this::getADC, array.length);
  }
}
