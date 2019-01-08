// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Urg04lxClearanceModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    ucm.first();
    ucm.last();
  }

  public void testNonCalib() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    assertTrue(ucm.putEvent().isPresent());
  }

  public void testSimple() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);
    ucm.getEvent(rimoGetEvent);
    assertTrue(ucm.putEvent().isPresent());
    SteerColumnInterface sci = new SteerColumnAdapter(false, Quantity.of(.2, "SCE"));
    assertTrue(Urg04lxClearanceHelper.isPathObstructed(sci, null));
  }

  public void testObstruction() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);
    ucm.getEvent(rimoGetEvent);
    assertTrue(ucm.putEvent().isPresent());
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.02, "SCE"));
    float[] array = new float[2];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    array[0] = 10;
    array[1] = 10;
    assertFalse(Urg04lxClearanceHelper.isPathObstructed(sci, floatBuffer));
    array[0] = 10;
    array[1] = 0;
    assertFalse(Urg04lxClearanceHelper.isPathObstructed(sci, floatBuffer));
    array[0] = 1;
    array[1] = 0;
    assertTrue(Urg04lxClearanceHelper.isPathObstructed(sci, floatBuffer));
  }

  public void testObstructionAngle() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);
    ucm.getEvent(rimoGetEvent);
    assertTrue(ucm.putEvent().isPresent());
    Scalar angle = RealScalar.of(-.02);
    float[] array = new float[2];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    array[0] = 10;
    array[1] = 10;
    assertFalse(Urg04lxClearanceHelper.isPathObstructed(angle, floatBuffer));
    array[0] = 10;
    array[1] = 0;
    assertFalse(Urg04lxClearanceHelper.isPathObstructed(angle, floatBuffer));
    array[0] = 1;
    array[1] = 0;
    assertTrue(Urg04lxClearanceHelper.isPathObstructed(angle, floatBuffer));
  }

  public void testRank() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    assertEquals(ucm.getProviderRank(), ProviderRank.EMERGENCY);
  }

  public void testNull() {
    Urg04lxClearanceModule ucm = new Urg04lxClearanceModule();
    ucm.lidarRayBlock(null);
    assertTrue(ucm.putEvent().isPresent());
  }
}
