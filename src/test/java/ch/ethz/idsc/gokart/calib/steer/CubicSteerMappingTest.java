// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class CubicSteerMappingTest extends TestCase {
  public void testAdvancedFormulaCenter() {
    SteerMapping steerMapping = CubicSteerMapping.approximation_1();
    Scalar angle = steerMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, Quantity.of(0, "SCE")));
    assertEquals(angle, RealScalar.ZERO);
    Scalar sce = steerMapping.getSCEfromAngle(angle);
    assertTrue(Scalars.isZero(sce));
  }

  public void testAdvancedFormulaSign() {
    SteerMapping steerMapping = CubicSteerMapping.approximation_1();
    Scalar sceIn = Quantity.of(0.1, "SCE");
    Scalar angle = steerMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, sceIn));
    assertTrue(Sign.isPositive(angle));
    Clip.function(.08, .15).requireInside(angle);
    Scalar sce = steerMapping.getSCEfromAngle(angle);
    assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.01, "SCE")));
  }

  public void testAdvancedFormulaNegative() {
    SteerMapping steerMapping = CubicSteerMapping.approximation_1();
    Scalar sceIn = Quantity.of(-0.7, "SCE");
    Scalar angle = steerMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, sceIn));
    assertTrue(Sign.isNegative(angle));
    Clip.function(-.5, -.4).requireInside(angle);
    Scalar sce = steerMapping.getSCEfromAngle(angle);
    assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.05, "SCE")));
  }

  public void testSceError() {
    SteerMapping steerMapping = CubicSteerMapping.approximation_1();
    Scalar max = Quantity.of(0, "SCE");
    for (Tensor s : Subdivide.of(-0.68847, 0.68847, 100)) {
      Scalar sceIn = Quantity.of(s.Get(), "SCE");
      Scalar angle = steerMapping.getAngleFromSCE(new SteerColumnAdapter(true, sceIn));
      Scalar error = sceIn.subtract(steerMapping.getSCEfromAngle(angle)).abs();
      max = Max.of(max, error);
    }
    assertTrue(Scalars.lessThan(max, Quantity.of(0.04, "SCE")));
  }

  public void testAngleError() {
    SteerMapping steerMapping = CubicSteerMapping.approximation_1();
    Scalar max = RealScalar.ZERO;
    for (Tensor s : Subdivide.of(-0.45, 0.45, 100)) {
      Scalar angleIn = s.Get();
      Scalar sce = steerMapping.getSCEfromAngle(angleIn);
      Scalar angle = steerMapping.getAngleFromSCE(new SteerColumnAdapter(true, sce));
      Scalar error = angleIn.subtract(angle).abs();
      max = Max.of(max, error);
    }
    assertTrue(Scalars.lessThan(max, RealScalar.of(0.011)));
  }
}
