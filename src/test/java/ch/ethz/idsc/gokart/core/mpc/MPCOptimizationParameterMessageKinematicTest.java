// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

public class MPCOptimizationParameterMessageKinematicTest extends TestCase {
  public void testSimple() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4 * 7]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putFloat(1f);
    byteBuffer.putFloat(2f);
    byteBuffer.putFloat(3f);
    byteBuffer.putFloat(4f);
    byteBuffer.putFloat(5f);
    byteBuffer.putFloat(6f);
    byteBuffer.putFloat(7f);
    byteBuffer.flip();
    MPCOptimizationParameterMessageKinematic mpcOptimizationParameterMessageDynamic = //
        new MPCOptimizationParameterMessageKinematic( //
            new MPCNativeSession(), new MPCOptimizationParameterKinematic(byteBuffer));
    assertEquals(mpcOptimizationParameterMessageDynamic.getMessageType(), MessageType.OPTIMIZATION_PARAMETER_KINEMATIC);
    {
      ByteBuffer restore = ByteBuffer.wrap(new byte[8 + 4 * 7]);
      restore.order(ByteOrder.LITTLE_ENDIAN);
      mpcOptimizationParameterMessageDynamic.insert(restore);
      restore.flip();
      assertEquals(restore.getInt(), 2); // MessageType.OPTIMIZATION_PARAMETER_KINEMATIC.ordinal()
      assertEquals(restore.getInt(), 0);
      assertEquals(restore.getFloat(), 1f);
      assertEquals(restore.getFloat(), 2f);
      assertEquals(restore.getFloat(), 3f);
      assertEquals(restore.getFloat(), 4f);
      assertEquals(restore.getFloat(), 5f);
      assertEquals(restore.getFloat(), 6f);
      assertEquals(restore.getFloat(), 7f);
    }
  }
}
