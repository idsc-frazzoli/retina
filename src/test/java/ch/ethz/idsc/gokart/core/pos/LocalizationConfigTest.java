// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.ParametricResample;
import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class LocalizationConfigTest extends TestCase {
  public void testHorizon() {
    Scalar emulation_deg = //
        Magnitude.DEGREE_ANGLE.apply(LocalizationConfig.GLOBAL.horizon);
    Clip.function(0.5, 1.0).requireInside(emulation_deg);
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
}
