// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Urg04lxClearanceModuleTest extends TestCase {
  public void testNonCalib() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    assertTrue(ucm.putEvent().isPresent());
  }

  public void testSimple() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
    ucm.getEvent(rimoGetEvent);
    assertTrue(ucm.putEvent().isPresent());
    SteerColumnInterface sci = new SteerColumnAdapter(false, Quantity.of(.2, "SCE"));
    assertTrue(StaticHelper.isPathObstructed(sci, null));
  }

  public void testObstruction() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
    ucm.getEvent(rimoGetEvent);
    assertTrue(ucm.putEvent().isPresent());
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.02, "SCE"));
    float[] array = new float[2];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    array[0] = 10;
    array[1] = 10;
    assertFalse(StaticHelper.isPathObstructed(sci, floatBuffer));
    array[0] = 10;
    array[1] = 0;
    assertFalse(StaticHelper.isPathObstructed(sci, floatBuffer));
    array[0] = 1;
    array[1] = 0;
    assertTrue(StaticHelper.isPathObstructed(sci, floatBuffer));
  }

  public void testObstructionAngle() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
    ucm.getEvent(rimoGetEvent);
    assertTrue(ucm.putEvent().isPresent());
    Scalar angle = RealScalar.of(-.02);
    float[] array = new float[2];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    array[0] = 10;
    array[1] = 10;
    assertFalse(StaticHelper.isPathObstructed(angle, floatBuffer));
    array[0] = 10;
    array[1] = 0;
    assertFalse(StaticHelper.isPathObstructed(angle, floatBuffer));
    array[0] = 1;
    array[1] = 0;
    assertTrue(StaticHelper.isPathObstructed(angle, floatBuffer));
  }

  public void testRank() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    assertEquals(ucm.getProviderRank(), ProviderRank.EMERGENCY);
  }
}
