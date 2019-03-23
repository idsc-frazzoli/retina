// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class GokartPoseEventV2 extends GokartPoseEventV1 {
  static final int LENGTH = 8 * 3 + 4 + 4 * 3;
  // ---
  private final float ux;
  private final float uy;
  private final float omega;

  /** @param byteBuffer */
  GokartPoseEventV2(ByteBuffer byteBuffer) {
    super(byteBuffer);
    ux = byteBuffer.getFloat();
    uy = byteBuffer.getFloat();
    omega = byteBuffer.getFloat();
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    super.insert(byteBuffer);
    byteBuffer.putFloat(ux);
    byteBuffer.putFloat(uy);
    byteBuffer.putFloat(omega);
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }

  @Override // from PoseVelocityInterface
  public Tensor getVelocityXY() {
    return Tensors.of( //
        Quantity.of(ux, SI.VELOCITY), //
        Quantity.of(uy, SI.VELOCITY) //
    );
  }

  @Override // from PoseVelocityInterface
  public Scalar getGyroZ() {
    return Quantity.of(omega, SI.PER_SECOND);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Join.of(super.asVector(), Tensors.vector(ux, uy, omega));
  }
}
