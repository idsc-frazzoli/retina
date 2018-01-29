// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

/** class design is similar to {@link GokartStatusEvent} */
public class GokartPoseEvent extends DataEvent implements GokartPoseInterface {
  static final int LENGTH = 8 * 3 + 4;
  private static final Unit METER = Unit.of("m");
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
    return Tensors.of(Quantity.of(x, METER), Quantity.of(y, METER), DoubleScalar.of(angle));
  }

  public Scalar getQuality() {
    return DoubleScalar.of(quality);
  }
}
