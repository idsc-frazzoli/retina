// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
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
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleTowards(Tensors.vector(1.19, 1));
    assertTrue(Chop._08.close(Quantity.of(1.5707963267948966, "rad"), angle));
  }

  public void testSteerAngleTowardsRight() {
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleTowards(Tensors.vector(1.19, -1));
    assertTrue(Chop._08.close(Quantity.of(-1.5707963267948966, "rad"), angle));
  }

  public void testSteerAngleTowardsStraight() {
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleTowards(Tensors.vector(1.19 + 1, 0));
    assertTrue(Chop._08.close(Quantity.of(0, "rad"), angle));
  }
}
