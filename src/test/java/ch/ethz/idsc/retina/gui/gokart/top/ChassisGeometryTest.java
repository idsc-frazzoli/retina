// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class ChassisGeometryTest extends TestCase {
  private static final ScalarUnaryOperator IN_CM = QuantityMagnitude.SI().in("cm");

  public void testSimple() {
    DifferentialSpeed differentialSpeed = //
        ChassisGeometry.GLOBAL.getDifferentialSpeed();
    differentialSpeed.pair(RealScalar.ONE, RealScalar.of(.3));
  }

  public void testSingleton() {
    Scalar xAxleDistance = ChassisGeometry.GLOBAL.xAxleDistanceMeter();
    Clip.function(1.1, 1.25).isInsideElseThrow(xAxleDistance);
    ChassisGeometry.GLOBAL.yTireFrontMeter();
  }

  public void testyHalfWidthMeter() {
    Scalar scalar = ChassisGeometry.GLOBAL.yHalfWidthMeter();
    assertTrue(scalar instanceof RealScalar);
  }

  public void testSteerAngleTowardsLeft() {
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(RealScalar.of(0.3));
    assertTrue(Chop._13.close(Quantity.of(0.34289723785565446, "rad"), angle));
  }

  public void testSteerAngleTowardsRight() {
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(RealScalar.of(-.2));
    assertTrue(Chop._13.close(Quantity.of(-0.2336530501796457, "rad"), angle));
  }

  public void testSteerAngleStraight() {
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(RealScalar.ZERO);
    assertTrue(Chop._13.close(Quantity.of(0, "rad"), angle));
  }

  public void testTireWidthFront() {
    Scalar width = ChassisGeometry.GLOBAL.tireHalfWidthFront.multiply(RealScalar.of(2));
    assertEquals(IN_CM.apply(width), RealScalar.of(13)); // cm
  }

  public void testTireWidthRear() {
    Scalar width = ChassisGeometry.GLOBAL.tireHalfWidthRear.multiply(RealScalar.of(2));
    assertEquals(IN_CM.apply(width), RealScalar.of(19.5)); // cm
  }

  public void testTireWidthContactFront() {
    Scalar width = ChassisGeometry.GLOBAL.tireHalfWidthContactFront.multiply(RealScalar.of(2));
    assertEquals(IN_CM.apply(width), RealScalar.of(9)); // cm
  }

  public void testTireWidthContactRear() {
    Scalar width = ChassisGeometry.GLOBAL.tireHalfWidthContactRear.multiply(RealScalar.of(2));
    assertEquals(IN_CM.apply(width), RealScalar.of(13.5)); // cm
  }
}
