// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.ParametricResample;
import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class LocalizationConfigTest extends TestCase {
  public void testHorizon() {
    Scalar emulation_deg = //
        Magnitude.DEGREE_ANGLE.apply(LocalizationConfig.GLOBAL.horizon);
    Clips.interval(0.5, 1.0).requireInside(emulation_deg);
  }

  public void testBits() {
    Scalar bitShift = LocalizationConfig.GLOBAL.bitShift;
    assertTrue(IntegerQ.of(bitShift));
    assertTrue(Sign.isPositiveOrZero(bitShift));
  }

  public void testGetUniformResample() {
    ParametricResample parametricResample = LocalizationConfig.GLOBAL.getResample();
    parametricResample.apply(RandomVariate.of(UniformDistribution.unit(), 10, 2));
  }

  public void testQualityOk() {
    assertFalse(LocalizationConfig.GLOBAL.isQualityOk(RealScalar.of(0.5)));
    assertTrue(LocalizationConfig.GLOBAL.isQualityOk(RealScalar.of(0.8)));
  }
}
