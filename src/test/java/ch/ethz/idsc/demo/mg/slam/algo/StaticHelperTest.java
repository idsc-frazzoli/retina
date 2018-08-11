// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.retina.util.math.Magnitude;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testPerMeter() {
    double turnRatePerMeter = Magnitude.PER_METER.apply(StaticHelper.TURNING_RATIO_MAX).number().doubleValue();
    assertEquals(turnRatePerMeter, 0.4082);
  }

  public void testSimple() {
    // TODO MG write test
    // StaticHelper.getAveragePose(slamParticles, relevantRange)
  }
}
