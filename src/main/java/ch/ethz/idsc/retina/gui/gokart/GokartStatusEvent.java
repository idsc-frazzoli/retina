// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

public class GokartStatusEvent extends DataEvent {
  public final float steeringAngle;

  public GokartStatusEvent(float steeringAngle) {
    this.steeringAngle = steeringAngle;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(steeringAngle);
  }

  @Override
  protected int length() {
    return 4;
  }
}
