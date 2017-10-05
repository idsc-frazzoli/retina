// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class GokartStatusEvent extends DataEvent {
  private final float steeringAngle;

  public GokartStatusEvent(float steeringAngle) {
    this.steeringAngle = steeringAngle;
  }

  public GokartStatusEvent(ByteBuffer byteBuffer) {
    steeringAngle = byteBuffer.getFloat();
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(steeringAngle);
  }

  @Override
  protected int length() {
    return 4;
  }

  public Scalar getSteeringAngle() {
    return RealScalar.of(steeringAngle);
  }
}
