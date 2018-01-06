// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** class design is similar to {@link GokartStatusEvent} */
public class GokartPoseEvent extends DataEvent implements GokartPoseInterface {
  private static final int LENGTH = 8 * 3;
  private static final ScalarUnaryOperator TO_METER = QuantityMagnitude.SI().in(Unit.of("m"));
  // ---
  // TODO isGlobal() info and getQuality() -> 0...1 of tracking
  private final double x;
  private final double y;
  private final double angle;

  /** @param pose vector of length 3 */
  public GokartPoseEvent(Tensor pose) {
    x = TO_METER.apply(pose.Get(0)).number().doubleValue();
    y = TO_METER.apply(pose.Get(1)).number().doubleValue();
    angle = pose.Get(2).number().doubleValue();
  }

  /** @param byteBuffer */
  public GokartPoseEvent(ByteBuffer byteBuffer) {
    x = byteBuffer.getDouble();
    y = byteBuffer.getDouble();
    angle = byteBuffer.getDouble();
  }

  @Override // from DataEvent
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.putDouble(x);
    byteBuffer.putDouble(y);
    byteBuffer.putDouble(angle);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return Tensors.of(Quantity.of(x, "m"), Quantity.of(y, "m"), DoubleScalar.of(angle));
  }
}
