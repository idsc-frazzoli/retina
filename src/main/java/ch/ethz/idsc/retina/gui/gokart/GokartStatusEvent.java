// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;

public class GokartStatusEvent extends DataEvent {
  /** raw value from encoder centered so that min == -max */
  private final float steerColumnEncoder;

  public GokartStatusEvent(float steerColumnAngle) {
    this.steerColumnEncoder = steerColumnAngle;
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

  public boolean isSteeringCalibrated() {
    return !Float.isNaN(steerColumnEncoder);
  }

  /** @return NaN if steering is not calibrated */
  public Scalar getSteeringAngle() {
    return UnitSystem.SI().apply( //
        Quantity.of(steerColumnEncoder, SteerPutEvent.UNIT_ENCODER) //
            .multiply(SteerConfig.GLOBAL.column2steer));
  }
}
