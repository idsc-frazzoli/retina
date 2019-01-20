// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class SteerMappingTest extends TestCase {
  private static final List<SteerMapping> STEER_MAPPINGS = Arrays.asList( //
      LinearSteerMapping.instance(), CubicSteerMapping.approximation_1());

  public void testAdvancedFormulaCenter() {
    for (SteerMapping steerMapping : STEER_MAPPINGS) {
      Scalar angle = steerMapping.getAngleFromSCE( //
          new SteerColumnAdapter(true, Quantity.of(0, "SCE")));
      assertEquals(angle, RealScalar.ZERO);
      Scalar sce = steerMapping.getSCEfromAngle(angle);
      assertTrue(Scalars.isZero(sce));
    }
  }

  public void testAdvancedFormulaSign() {
    for (SteerMapping steerMapping : STEER_MAPPINGS) {
      Scalar sceIn = Quantity.of(0.1, "SCE");
      Scalar angle = steerMapping.getAngleFromSCE( //
          new SteerColumnAdapter(true, sceIn));
      assertTrue(Sign.isPositive(angle));
      Clip.function(.05, .1).requireInside(angle);
      Scalar sce = steerMapping.getSCEfromAngle(angle);
      assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.01, "SCE")));
    }
  }

  public void testAdvancedFormulaNegative() {
    for (SteerMapping steerMapping : STEER_MAPPINGS) {
      Scalar sceIn = Quantity.of(-0.7, "SCE");
      Scalar angle = steerMapping.getAngleFromSCE( //
          new SteerColumnAdapter(true, sceIn));
      assertTrue(Sign.isNegative(angle));
      Clip.function(-.5, -.4).requireInside(angle);
      Scalar sce = steerMapping.getSCEfromAngle(angle);
      assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.05, "SCE")));
    }
  }

  public void testContainment() {
    assertTrue(STEER_MAPPINGS.contains(SteerConfig.GLOBAL.getSteerMapping()));
  }
}
