// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.VelocityHelper;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/** when localization was introduced on 2017-12-18
 * the pose was published without pose-quality */
/* package */ class GokartPoseEventV0 extends DataEvent implements GokartPoseEvent {
  static final int LENGTH = Double.BYTES * 3;
  static final Scalar QUALITY_UNKNOWN = DoubleScalar.of(0.25);
  private static final Scalar GYROZ_ZERO = Quantity.of(0.0, SI.PER_SECOND);
  // ---
  private final double x;
  private final double y;
  private final double angle;

  /** @param byteBuffer */
  GokartPoseEventV0(ByteBuffer byteBuffer) {
    x = byteBuffer.getDouble();
    y = byteBuffer.getDouble();
    angle = byteBuffer.getDouble();
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putDouble(x);
    byteBuffer.putDouble(y);
    byteBuffer.putDouble(angle);
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
  public Scalar getQuality() {
    return QUALITY_UNKNOWN;
  }

  @Override // from GokartPoseEvent
  public final boolean hasVelocity() {
    return this instanceof GokartPoseEventV2;
  }

  @Override // from PoseVelocityInterface
  public Tensor getVelocity() {
    return VelocityHelper.ZERO;
  }

  @Override // from PoseVelocityInterface
  public Scalar getGyroZ() {
    return GYROZ_ZERO;
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector(x, y, angle).map(Round._6);
  }
}
