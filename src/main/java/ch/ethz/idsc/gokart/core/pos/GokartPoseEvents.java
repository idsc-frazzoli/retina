// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum GokartPoseEvents {
  ;
  public static GokartPoseEvent getPoseEvent(Tensor state, Scalar quality) {
    Tensor _state = state;
    byte[] array = new byte[GokartPoseEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(Magnitude.METER.toDouble(_state.Get(0)));
    byteBuffer.putDouble(Magnitude.METER.toDouble(_state.Get(1)));
    byteBuffer.putDouble(Magnitude.ONE.apply(_state.Get(2)).number().doubleValue());
    byteBuffer.putFloat(quality.number().floatValue());
    byteBuffer.flip();
    return new GokartPoseEvent(byteBuffer);
  }
}
