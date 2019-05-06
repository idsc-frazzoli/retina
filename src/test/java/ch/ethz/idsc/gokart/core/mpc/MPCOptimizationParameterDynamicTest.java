// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MPCOptimizationParameterDynamicTest extends TestCase {
  public void testBasic() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putFloat(1.5f);
    byteBuffer.putFloat(2.5f);
    byteBuffer.putFloat(3.5f);
    byteBuffer.putFloat(4.5f);
    byteBuffer.flip();
    MPCOptimizationParameterDynamic mpcOptimizationParameterDynamic = //
        new MPCOptimizationParameterDynamic(byteBuffer);
    assertEquals(mpcOptimizationParameterDynamic.speedLimit(), Quantity.of(1.5, SI.VELOCITY));
    assertEquals(mpcOptimizationParameterDynamic.xAccLimit(), Quantity.of(2.5, SI.ACCELERATION));
  }
}
