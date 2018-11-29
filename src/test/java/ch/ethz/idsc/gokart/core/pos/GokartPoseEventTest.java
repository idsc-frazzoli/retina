// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class GokartPoseEventTest extends TestCase {
  public void testSimple() {
    Tensor pose = Tensors.fromString("{1[m],2[m],3}");
    GokartPoseEvent gokartPoseEvent = GokartPoseEvents.getPoseEvent(pose, RealScalar.ONE);
    assertEquals(gokartPoseEvent.getPose(), pose);
    assertEquals(gokartPoseEvent.length(), 28);
  }

  public void testFailUnits() {
    Tensor pose = Tensors.fromString("{1[m],2[m],3[m]}");
    try {
      GokartPoseEvents.getPoseEvent(pose, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
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
    assertEquals(gpe.getQuality(), RealScalar.of(0.6f));
    Arrays.equals(array, gpe.asArray());
  }

  public void testReconstruction() {
    Tensor pose = Tensors.fromString("{1[m],2[m],3}");
    GokartPoseEvent gokartPoseEvent = GokartPoseEvents.getPoseEvent(pose, RealScalar.of(.7));
    byte[] array = gokartPoseEvent.asArray();
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    GokartPoseEvent gpe = new GokartPoseEvent(byteBuffer);
    assertEquals(gpe.getPose(), pose);
    assertEquals(gpe.getQuality(), RealScalar.of(.7f));
  }

  public void testExtract() {
    ScalarUnaryOperator suo = QuantityMagnitude.SI().in(Unit.ONE);
    assertEquals(suo.apply(RealScalar.of(123)), RealScalar.of(123));
    assertEquals(suo.apply(Quantity.of(2, "rad")), RealScalar.of(2));
    try {
      suo.apply(Quantity.of(2, SI.SECOND));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
