// code by mh
package ch.ethz.idsc.gokart.core.ekf;

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
 * an instance of {@link GokartStateEvent} is immutable */
public class GokartVelocityEvent extends DataEvent implements GokartVelocityInterface {
  static final int LENGTH = 8 * 3;
  // ---
  // TODO isGlobal() info and getQuality() -> 0...1 of tracking
  private final double dotX;
  private final double dotY;
  private final double angularVelocity;

  /** @param byteBuffer */
  public GokartVelocityEvent(ByteBuffer byteBuffer) {
    dotX = byteBuffer.getDouble();
    dotY = byteBuffer.getDouble();
    angularVelocity = byteBuffer.getDouble();
  }

  @Override // from DataEvent
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.putDouble(dotX);
    byteBuffer.putDouble(dotY);
    byteBuffer.putDouble(angularVelocity);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  @Override // from GokartPoseInterface
  public Tensor getVelocity() {
    return Tensors.of( //
        Quantity.of(dotX, SI.VELOCITY), //
        Quantity.of(dotY, SI.VELOCITY), //
        Quantity.of(angularVelocity, SI.PER_SECOND));
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector(dotX, dotY, angularVelocity);
  }
}
