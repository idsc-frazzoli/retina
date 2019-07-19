// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.util.Random;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class FittedSteerMappingTest extends TestCase {
  public void testAdvancedFormulaCenter() {
    SteerMapping steerMapping = FittedSteerMapping.instance();
    Scalar ratio = steerMapping.getRatioFromSCE( //
        new SteerColumnAdapter(true, Quantity.of(0, "SCE")));
    assertEquals(ratio, Quantity.of(0, SI.PER_METER));
    Scalar sce = steerMapping.getSCEfromRatio(ratio);
    assertTrue(Scalars.isZero(sce));
  }

  public void testAdvancedFormulaSign() {
    SteerMapping steerMapping = FittedSteerMapping.instance();
    Scalar sceIn = Quantity.of(0.1, "SCE");
    Scalar ratio = steerMapping.getRatioFromSCE( //
        new SteerColumnAdapter(true, sceIn));
    assertTrue(Sign.isPositive(ratio));
    Clips.interval(.08, .15).requireInside(Magnitude.PER_METER.apply(ratio));
    Scalar sce = steerMapping.getSCEfromRatio(ratio);
    assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.01, "SCE")));
  }

  public void testAdvancedFormulaNegative() {
    SteerMapping steerMapping = FittedSteerMapping.instance();
    Scalar sceIn = Quantity.of(-0.7, "SCE");
    Scalar ratio = steerMapping.getRatioFromSCE( //
        new SteerColumnAdapter(true, sceIn));
    assertTrue(Sign.isNegative(ratio));
    Clips.interval(-.5, -.4).requireInside(Magnitude.PER_METER.apply(ratio));
    Scalar sce = steerMapping.getSCEfromRatio(ratio);
    assertTrue(Scalars.lessThan(sce.subtract(sceIn).abs(), Quantity.of(0.05, "SCE")));
  }

  public void testSceError() {
    SteerMapping steerMapping = FittedSteerMapping.instance();
    Scalar max = Quantity.of(0, "SCE");
    for (Tensor s : Subdivide.of(-0.68847, 0.68847, 100)) {
      Scalar sceIn = Quantity.of(s.Get(), "SCE");
      Scalar ratio = steerMapping.getRatioFromSCE(new SteerColumnAdapter(true, sceIn));
      Scalar error = sceIn.subtract(steerMapping.getSCEfromRatio(ratio)).abs();
      max = Max.of(max, error);
    }
    assertTrue(Scalars.lessThan(max, Quantity.of(0.04, "SCE")));
  }

  public void testAngleError() {
    SteerMapping steerMapping = FittedSteerMapping.instance();
    Scalar max = Quantity.of(0, SI.PER_METER);
    for (Tensor s : Subdivide.of(Quantity.of(-0.45, SI.PER_METER), Quantity.of(0.45, SI.PER_METER), 100)) {
      Scalar ratioIn = s.Get();
      Scalar sce = steerMapping.getSCEfromRatio(ratioIn);
      Scalar ratio = steerMapping.getRatioFromSCE(new SteerColumnAdapter(true, sce));
      Scalar error = ratioIn.subtract(ratio).abs();
      max = Max.of(max, error);
    }
    assertTrue(Scalars.lessThan(max, Quantity.of(0.011, SI.PER_METER)));
  }

  public void testRootReal() {
    SteerMapping steerMapping = FittedSteerMapping.instance();
    Random random = new Random();
    Tensor tensor = Subdivide.of(Quantity.of(-0.5, "m^-1"), Quantity.of(+0.5, "m^-1"), 100 + random.nextInt(100)) //
        .map(steerMapping::getSCEfromRatio);
    assertTrue(Chop.NONE.allZero(Imag.of(tensor)));
  }
}
