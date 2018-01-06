// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GokartPoseEventTest extends TestCase {
  public void testSimple() {
    Tensor pose = Tensors.fromString("{1[m],2[m],3}");
    GokartPoseEvent gokartPoseEvent = GokartPoseEvents.getPoseEvent(pose, RealScalar.ONE);
    assertEquals(gokartPoseEvent.getPose(), pose);
    assertEquals(gokartPoseEvent.length(), 28);
  }

  public void testByteBuffer() {
    byte[] array = new byte[24 + 4];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(2.0);
    byteBuffer.putDouble(4.0);
    byteBuffer.putDouble(5.0);
    byteBuffer.putFloat(0.6f);
    byteBuffer.flip();
    GokartPoseEvent gpe = new GokartPoseEvent(byteBuffer);
    assertEquals(gpe.getPose(), Tensors.fromString("{2[m],4[m],5}"));
    Arrays.equals(array, gpe.asArray());
  }
}
