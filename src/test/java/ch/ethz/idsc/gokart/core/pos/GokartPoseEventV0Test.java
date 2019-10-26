// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import junit.framework.TestCase;

public class GokartPoseEventV0Test extends TestCase {
  public void testLength() {
    assertEquals(GokartPoseEventV0.LENGTH, 24);
  }

  public void testPoseQualityLow() {
    assertFalse(LocalizationConfig.GLOBAL.isQualityOk(GokartPoseEventV0.QUALITY_UNKNOWN));
  }
}
