// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** class design is similar to {@link GokartStatusEvent}
 * 
 * an instance of {@link GokartPoseEvent} is immutable */
public class GokartPoseEvent extends DataEvent implements GokartPoseInterface {
  static final int LENGTH = 8 * 3 + 4;
  // ---
  // TODO isGlobal() info and getQuality() -> 0...1 of tracking
  private final double x;
  private final double y;
  private final double angle;
  private final float quality;

  /** @param byteBuffer */
  public GokartPoseEvent(ByteBuffer byteBuffer) {
    x = byteBuffer.getDouble();
    y = byteBuffer.getDouble();
    angle = byteBuffer.getDouble();
    quality = byteBuffer.getFloat();
  }

  @Override // from DataEvent
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.putDouble(x);
    byteBuffer.putDouble(y);
    byteBuffer.putDouble(angle);
    byteBuffer.putFloat(quality);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return Tensors.of( //
        Quantity.of(x, SI.METER), //
        Quantity.of(y, SI.METER), //
        DoubleScalar.of(angle));
  }

  /** @return value in the interval [0, 1] where
   * 0 represents no pose quality, and
   * 1 represents perfect pose quality */
  public Scalar getQuality() {
    return DoubleScalar.of(quality);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector(x, y, angle, quality);
  }
}
