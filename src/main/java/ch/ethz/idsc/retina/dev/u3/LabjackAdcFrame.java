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

/** immutable */
public final class LabjackAdcFrame extends DataEvent implements Serializable {
  /** log file analysis shows that the throttle signal at AIN2
   * ranges from {-0.075455[V], 5.11837[V]}.
   * the lower bound is deliberately increased so that the lower bound
   * is insensitive to noise. */
  private static final Clip THROTTLE_CLIP = Clip.function(0.1, 5.11);
  private static final int THROTTLE_INDEX = 2;
  /** 0.3[V] when not pressed, 2.45[V] */
  private static final float BOOST_BUTTON_TRESHOLD = 1f;
  private static final int BOOST_BUTTON_INDEX = 4;
  // ---
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

  /** @return value in the interval [-1, 1] */
  public Scalar getAheadSigned() {
    Scalar scalar = getThrottle();
    return isBoostPressed() //
        ? scalar.negate()
        : scalar;
  }

  /** @return value in the interval [0, 1] */
  public Scalar getThrottle() {
    return THROTTLE_CLIP.rescale(RealScalar.of(array[THROTTLE_INDEX]));
  }

  /** @return */
  public boolean isBoostPressed() {
    return BOOST_BUTTON_TRESHOLD < array[BOOST_BUTTON_INDEX];
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vectorFloat(array);
  }
}
