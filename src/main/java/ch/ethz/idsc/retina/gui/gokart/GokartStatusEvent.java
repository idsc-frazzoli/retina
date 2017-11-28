// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;

/** the capabilities of gokart status event include
 * {@link SteerColumnInterface} */
public class GokartStatusEvent extends DataEvent implements SteerColumnInterface {
  /** raw value from encoder centered so that min == -max */
  private final float steerColumnEncoder;

  public GokartStatusEvent(float steerColumnEncoder) {
    this.steerColumnEncoder = steerColumnEncoder;
  }

  public GokartStatusEvent(ByteBuffer byteBuffer) {
    steerColumnEncoder = byteBuffer.getFloat();
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(steerColumnEncoder);
  }

  @Override
  protected int length() {
    return 4;
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

  /** @return scalar without unit but with interpretation in radians,
   * NaN if steering is not calibrated */
  public Scalar getSteeringAngle() {
    return UnitSystem.SI().apply( //
        getSteerColumnEncoderCentered().multiply(SteerConfig.GLOBAL.column2steer));
  }
}
