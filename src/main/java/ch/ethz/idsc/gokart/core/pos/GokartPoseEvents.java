// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum GokartPoseEvents {
  ;
  /** @param pose {x[m], y[m], alpha}
   * @param quality in the interval [0, 1]
   * @return */
  public static GokartPoseEvent getPoseEvent(Tensor pose, Scalar quality) {
    byte[] array = new byte[GokartPoseEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(Magnitude.METER.toDouble(pose.Get(0)));
    byteBuffer.putDouble(Magnitude.METER.toDouble(pose.Get(1)));
    byteBuffer.putDouble(Magnitude.ONE.toDouble(pose.Get(2)));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(quality));
    byteBuffer.flip();
    return new GokartPoseEvent(byteBuffer);
  }
}
