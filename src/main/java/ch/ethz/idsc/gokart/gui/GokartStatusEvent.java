// code by jph
package ch.ethz.idsc.gokart.gui;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** the capabilities of gokart status event include
 * {@link SteerColumnInterface} */
public class GokartStatusEvent extends DataEvent implements SteerColumnInterface {
  private static final int LENGTH = 4;
  // ---
  /** raw value from encoder centered so that min == -max */
  private final float steerColumnEncoder;

  public GokartStatusEvent(float steerColumnEncoder) {
    this.steerColumnEncoder = steerColumnEncoder;
  }

  public GokartStatusEvent(ByteBuffer byteBuffer) {
    steerColumnEncoder = byteBuffer.getFloat();
  }

  @Override // from DataEvent
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(steerColumnEncoder);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  @Override // from SteerColumnInterface
  public boolean isSteerColumnCalibrated() {
    return !Float.isNaN(steerColumnEncoder);
  }

  @Override // from SteerColumnInterface
  public Scalar getSteerColumnEncoderCentered() {
    if (!isSteerColumnCalibrated())
      throw new RuntimeException();
    return Quantity.of(steerColumnEncoder, SteerPutEvent.UNIT_ENCODER);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector(steerColumnEncoder);
  }
}
