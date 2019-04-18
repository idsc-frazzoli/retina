// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.Magnitude;
import junit.framework.TestCase;

public class MPCOptimizationConfigTest extends TestCase {
  public void testSimple() {
    // ensures that specificMoI has unit [m]
    Magnitude.METER.apply(MPCOptimizationConfig.GLOBAL.specificMoI);
  }
}
