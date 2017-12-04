// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class ChassisGeometryTest extends TestCase {
  public void testSimple() {
    DifferentialSpeed differentialSpeed = //
        ChassisGeometry.GLOBAL.getDifferentialSpeed();
    differentialSpeed.pair(RealScalar.ONE, RealScalar.of(.3));
  }

  public void testSingleton() {
    ChassisGeometry.GLOBAL.xAxleDistanceMeter();
    ChassisGeometry.GLOBAL.xAxleRearMeter();
    ChassisGeometry.GLOBAL.yTireFrontMeter();
  }
}
