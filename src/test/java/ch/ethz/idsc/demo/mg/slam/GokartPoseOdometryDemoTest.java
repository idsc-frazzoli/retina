// code by jph
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartPoseOdometryDemoTest extends TestCase {
  public void testSimple() {
    GokartPoseOdometryDemo demo = GokartPoseOdometryDemo.create();
    Tensor angularRate_Y_pair = Tensors.fromString("{5[rad*s^-1],14[rad*s^-1]}");
    demo.step(angularRate_Y_pair);
    Tensor velocity = demo.getVelocity();
    assertEquals(velocity, Tensors.fromString("{1.14[m*s^-1], 0[m*s^-1], 1.0[s^-1]}"));
  }

  public void testPreviousImpl() {
    Tensor angularRate_Y_pair = Tensors.fromString("{5[rad*s^-1],14[rad*s^-1]}");
    Tensor velocity = computeVelocity(angularRate_Y_pair);
    assertEquals(velocity, Tensors.fromString("{1.14[m*s^-1], 0[m*s^-1], 1.0[s^-1]}"));
  }

  private static Tensor computeVelocity(Tensor angularRate_Y_pair) {
    Tensor speed_pair = angularRate_Y_pair.multiply(ChassisGeometry.GLOBAL.tireRadiusRear);
    Scalar speedL = speed_pair.Get(0);
    Scalar speedR = speed_pair.Get(1);
    Scalar HALF = DoubleScalar.of(0.5);
    Scalar speed = speedL.add(speedR).multiply(HALF);
    Scalar rate = speedR.subtract(speedL).multiply(HALF).divide(ChassisGeometry.GLOBAL.yTireRear);
    return Tensors.of(speed, Quantity.of(0, SI.VELOCITY), rate);
  }

  private static void checkUnits(Tensor velocity) {
    VectorQ.ofLength(velocity, 3);
    Magnitude.VELOCITY.apply(velocity.Get(0));
    Magnitude.VELOCITY.apply(velocity.Get(1));
    Magnitude.PER_SECOND.apply(velocity.Get(2));
  }

  public void testInitial() {
    GokartPoseOdometryDemo demo = GokartPoseOdometryDemo.create();
    checkUnits(demo.getVelocity());
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(100, 200);
    demo.getEvent(rimoGetEvent);
    checkUnits(demo.getVelocity());
  }

  public void testPoseUnitFail() {
    GokartPoseOdometryDemo demo = GokartPoseOdometryDemo.create();
    try {
      demo.setPose(Array.zeros(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
