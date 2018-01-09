// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum GokartPoseEvents {
  ;
  private static final ScalarUnaryOperator TO_METER = QuantityMagnitude.SI().in(Unit.of("m"));
  private static final ScalarUnaryOperator TO_RADIANS = QuantityMagnitude.SI().in(Unit.ONE);

  public static GokartPoseEvent getPoseEvent(Tensor state, Scalar quality) {
    Tensor _state = state;
    byte[] array = new byte[GokartPoseEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(TO_METER.apply(_state.Get(0)).number().doubleValue());
    byteBuffer.putDouble(TO_METER.apply(_state.Get(1)).number().doubleValue());
    byteBuffer.putDouble(TO_RADIANS.apply(_state.Get(2)).number().doubleValue());
    byteBuffer.putFloat(quality.number().floatValue());
    byteBuffer.flip();
    return new GokartPoseEvent(byteBuffer);
  }
}
