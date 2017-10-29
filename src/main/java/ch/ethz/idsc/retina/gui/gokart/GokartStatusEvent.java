// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class GokartStatusEvent extends DataEvent {
  private final float steerColumnAngle;

  public GokartStatusEvent(float steerColumnAngle) {
    this.steerColumnAngle = steerColumnAngle;
  }

  public GokartStatusEvent(ByteBuffer byteBuffer) {
    steerColumnAngle = byteBuffer.getFloat();
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(steerColumnAngle);
  }

  @Override
  protected int length() {
    return 4;
  }

  public boolean isSteeringCalibrated() {
    return !Float.isNaN(steerColumnAngle);
  }

  /** @return NaN if steering is not calibrated */
  public Scalar getSteeringAngle() {
    return RealScalar.of(steerColumnAngle).multiply(SteerConfig.GLOBAL.column2steer);
  }
}
