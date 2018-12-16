// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class LinmotPutOperationTest extends TestCase {
  public void testSimple() {
    LinmotPutEvent lpe = LinmotPutOperation.INSTANCE.generic( //
        LinmotPutHelper.CMD_ERR_ACK, //
        LinmotPutHelper.MC_ZEROS, //
        (short) 1, (short) 2, (short) 3, (short) 4);
    assertEquals(lpe.target_position, 1);
    assertEquals(lpe.max_velocity, 2);
    assertEquals(lpe.acceleration, 3);
    assertEquals(lpe.deceleration, 4);
  }

  public void testIncrement() {
    byte my1 = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(.3)).getMotionCmdHeaderCounter();
    byte my2 = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(.3)).getMotionCmdHeaderCounter();
    byte my3 = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(.5)).getMotionCmdHeaderCounter();
    byte my4 = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(.5)).getMotionCmdHeaderCounter();
    byte my5 = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(.3)).getMotionCmdHeaderCounter();
    assertEquals(my1, my2);
    assertFalse(my1 == my3);
    assertEquals(my3, my4);
    assertFalse(my1 == my5);
    assertFalse(my4 == my5);
  }

  public void testException() {
    try {
      LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(-.3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
