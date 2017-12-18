// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ChassisGeometryTest extends TestCase {
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
}
