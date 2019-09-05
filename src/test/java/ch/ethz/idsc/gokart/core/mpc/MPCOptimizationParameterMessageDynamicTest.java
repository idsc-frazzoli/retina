// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

public class MPCOptimizationParameterMessageDynamicTest extends TestCase {
  public void testSimple() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4 * 4]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putFloat(1f);
    byteBuffer.putFloat(2f);
    byteBuffer.putFloat(3f);
    byteBuffer.putFloat(4f);
    byteBuffer.flip();
    MPCOptimizationParameterMessageDynamic mpcOptimizationParameterMessageDynamic = //
        new MPCOptimizationParameterMessageDynamic( //
            new MPCNativeSession(), new MPCOptimizationParameterDynamic(byteBuffer));
    assertEquals(mpcOptimizationParameterMessageDynamic.getMessageType(), MessageType.OPTIMIZATION_PARAMETER_DYNAMIC);
    {
      ByteBuffer restore = ByteBuffer.wrap(new byte[8 + 4 * 4]);
      restore.order(ByteOrder.LITTLE_ENDIAN);
      mpcOptimizationParameterMessageDynamic.insert(restore);
      restore.flip();
      assertEquals(restore.getInt(), 4); // MessageType.OPTIMIZATION_PARAMETER_DYNAMIC.ordinal()
      assertEquals(restore.getInt(), 0);
      assertEquals(restore.getFloat(), 1f);
      assertEquals(restore.getFloat(), 2f);
      assertEquals(restore.getFloat(), 3f);
      assertEquals(restore.getFloat(), 4f);
    }
  }
}
