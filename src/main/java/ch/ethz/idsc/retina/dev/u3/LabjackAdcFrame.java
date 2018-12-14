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
  // TODO put magic const into config file
  private static final Clip CLIP = Clip.function(0.1, 5);
  // ---
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

  /** @return value in the interval [-1, 1] */
  public Scalar getAheadSigned() {
    Scalar scalar = getThrottle();
    return isBoostPressed() ? scalar.negate() : scalar;
  }

  /** @return value in the interval [0, 1] */
  public Scalar getThrottle() {
    return CLIP.rescale(RealScalar.of(array[2]));
  }

  /** @return */
  public boolean isBoostPressed() {
    return 1 < array[4]; // 0.3 when not pressed
  }
}
