// code by jph
package ch.ethz.idsc.gokart.core.ekf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

public enum GokartVelocityEvents {
  ;
  /** @param velocity {dotX[m/s], dotY[m/s], angular velocity[1/s]}
   * @return */
  public static GokartVelocityEvent getPoseEvent(Tensor velocity) {
    byte[] array = new byte[GokartVelocityEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(Magnitude.VELOCITY.toDouble(velocity.Get(0)));
    byteBuffer.putDouble(Magnitude.VELOCITY.toDouble(velocity.Get(1)));
    byteBuffer.putDouble(Magnitude.PER_SECOND.toDouble(velocity.Get(2)));
    byteBuffer.flip();
    return new GokartVelocityEvent(byteBuffer);
  }
}
