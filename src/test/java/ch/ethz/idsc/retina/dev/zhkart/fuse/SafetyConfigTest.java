// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SafetyConfigTest extends TestCase {
  public void testVlp16Lo() {
    Clip.function(-1, -0.5).isInsideElseThrow(SafetyConfig.GLOBAL.vlp16_ZLoMeter());
    Clip.function(0, 0.3).isInsideElseThrow(SafetyConfig.GLOBAL.vlp16_ZHiMeter());
  }
}
