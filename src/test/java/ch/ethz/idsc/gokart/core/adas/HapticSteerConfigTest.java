// code by jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class HapticSteerConfigTest extends TestCase {
  public void testSimple() {
    HapticSteerConfig.GLOBAL.latForceCompensationBoundaryClip();
  }

  public void testTsuFactor() {
    Clips.unit().requireInside(HapticSteerConfig.GLOBAL.tsuFactor);
  }
}
