// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class GokartPoseEventV1 extends DataEvent implements GokartPoseEvent {
  static final int LENGTH = 8 * 3 + 4;
  static final Tensor VELOCITY_ZERO = Tensors.of( //
      Quantity.of(0.0, SI.VELOCITY), //
      Quantity.of(0.0, SI.VELOCITY)).unmodifiable();
  static final Scalar GYROZ_ZERO = Quantity.of(0.0, SI.PER_SECOND);
  // ---
  private final double x;
  private final double y;
  private final double angle;
  private final float quality;

  /** @param byteBuffer */
  GokartPoseEventV1(ByteBuffer byteBuffer) {
    x = byteBuffer.getDouble();
    y = byteBuffer.getDouble();
    angle = byteBuffer.getDouble();
    quality = byteBuffer.getFloat();
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putDouble(x);
    byteBuffer.putDouble(y);
    byteBuffer.putDouble(angle);
    byteBuffer.putFloat(quality);
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }

  @Override // from GokartPoseInterface
  public final Tensor getPose() {
    return Tensors.of( //
        Quantity.of(x, SI.METER), //
        Quantity.of(y, SI.METER), //
        DoubleScalar.of(angle));
  }

  @Override // from GokartPoseEvent
  public final Scalar getQuality() {
    return DoubleScalar.of(quality);
  }

  @Override // from GokartPoseEvent
  public final boolean hasVelocity() {
    return this instanceof GokartPoseEventV2;
  }

  @Override // from PoseVelocityInterface
  public Tensor getVelocityXY() {
    return VELOCITY_ZERO.copy();
  }

  @Override // from PoseVelocityInterface
  public Scalar getGyroZ() {
    return GYROZ_ZERO;
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector(x, y, angle).map(Round._6).append(RealScalar.of(quality).map(Round._3));
  }
}
