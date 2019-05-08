// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class CubicAngleMappingTest extends TestCase {
  public void testAdvancedFormulaCenter() {
    AngleMapping angleMapping = CubicAngleMapping.instance();
    Scalar ratio = angleMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, Quantity.of(0, "SCE")));
    assertEquals(ratio, Quantity.of(0, SI.ONE));
    Scalar sce = angleMapping.getSCEfromAngle(ratio);
    assertTrue(Scalars.isZero(sce));
  }

  public void testAdvancedFormulaSign() {
    AngleMapping angleMapping = CubicAngleMapping.instance();
    Scalar sceIn = Quantity.of(0.1, "SCE");
    Scalar ratio = angleMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, sceIn));
    assertTrue(Sign.isPositive(ratio));
    Clips.interval(.08, .15).requireInside(ratio);
    Scalar sce = angleMapping.getSCEfromAngle(ratio);
    assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.01, "SCE")));
  }

  public void testAdvancedFormulaNegative() {
    AngleMapping angleMapping = CubicAngleMapping.instance();
    Scalar sceIn = Quantity.of(-0.7, "SCE");
    Scalar angle = angleMapping.getAngleFromSCE( //
        new SteerColumnAdapter(true, sceIn));
    assertTrue(Sign.isNegative(angle));
    Clips.interval(-.5, -.4).requireInside(angle);
    Scalar sce = angleMapping.getSCEfromAngle(angle);
    assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.05, "SCE")));
  }

  public void testSceError() {
    AngleMapping angleMapping = CubicAngleMapping.instance();
    Scalar max = Quantity.of(0, "SCE");
    for (Tensor s : Subdivide.of(-0.68847, 0.68847, 100)) {
      Scalar sceIn = Quantity.of(s.Get(), "SCE");
      Scalar angle = angleMapping.getAngleFromSCE(new SteerColumnAdapter(true, sceIn));
      Scalar error = sceIn.subtract(angleMapping.getSCEfromAngle(angle)).abs();
      max = Max.of(max, error);
    }
    assertTrue(Scalars.lessThan(max, Quantity.of(0.04, "SCE")));
  }

  public void testAngleError() {
    AngleMapping angleMapping = CubicAngleMapping.instance();
    Scalar max = Quantity.of(0, SI.ONE);
    for (Tensor s : Subdivide.of(Quantity.of(-0.45, SI.ONE), Quantity.of(0.45, SI.ONE), 100)) {
      Scalar ratioIn = s.Get();
      Scalar sce = angleMapping.getSCEfromAngle(ratioIn);
      Scalar ratio = angleMapping.getAngleFromSCE(new SteerColumnAdapter(true, sce));
      Scalar error = ratioIn.subtract(ratio).abs();
      max = Max.of(max, error);
    }
    assertTrue(Scalars.lessThan(max, Quantity.of(0.011, SI.ONE)));
  }
}
