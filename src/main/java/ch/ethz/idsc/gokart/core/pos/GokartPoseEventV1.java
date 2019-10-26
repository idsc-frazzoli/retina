// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

/** from 2018-01-08 the pose is published with pose-quality */
/* package */ class GokartPoseEventV1 extends GokartPoseEventV0 {
  static final int LENGTH = GokartPoseEventV0.LENGTH + Float.BYTES;
  // ---
  private final float quality;

  /** @param byteBuffer */
  GokartPoseEventV1(ByteBuffer byteBuffer) {
    super(byteBuffer);
    quality = byteBuffer.getFloat();
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    super.insert(byteBuffer);
    byteBuffer.putFloat(quality);
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }

  @Override // from GokartPoseEvent
  public final Scalar getQuality() {
    return DoubleScalar.of(quality);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return super.asVector().append(RealScalar.of(quality).map(Round._3));
  }
}
