// code by jph
package ch.ethz.idsc.gokart.core.adas;

import junit.framework.TestCase;

public class HapticSteerConfigTest extends TestCase {
  public void testSimple() {
    HapticSteerConfig.GLOBAL.criticalAngle();
    HapticSteerConfig.GLOBAL.criticalSlipClip();
    HapticSteerConfig.GLOBAL.dynamicCompensationBoundaryClip();
    HapticSteerConfig.GLOBAL.latForceCompensationBoundaryClip();
  }
}
