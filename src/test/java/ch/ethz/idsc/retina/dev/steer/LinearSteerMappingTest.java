// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class LinearSteerMappingTest extends TestCase {
  public void testAdvancedFormulaCenter() {
    SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
    Scalar angle = steerMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, Quantity.of(0, "SCE")));
    assertEquals(angle, RealScalar.ZERO);
    Scalar sce = steerMapping.getSCEfromAngle(angle);
    assertTrue(Scalars.isZero(sce));
  }

  public void testAdvancedFormulaSign() {
    SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
    Scalar sceIn = Quantity.of(0.1, "SCE");
    Scalar angle = steerMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, sceIn));
    assertTrue(Sign.isPositive(angle));
    Clip.function(.05, .07).requireInside(angle);
    Scalar sce = steerMapping.getSCEfromAngle(angle);
    assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.01, "SCE")));
  }

  public void testAdvancedFormulaNegative() {
    SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
    Scalar sceIn = Quantity.of(-0.7, "SCE");
    Scalar angle = steerMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, sceIn));
    assertTrue(Sign.isNegative(angle));
    Clip.function(-.5, .4).requireInside(angle);
    Scalar sce = steerMapping.getSCEfromAngle(angle);
    assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.05, "SCE")));
  }
}
