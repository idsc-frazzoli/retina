// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;

import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import junit.framework.TestCase;

public class PursuitConfigTest extends TestCase {
  public void testSimple() {
    List<DynamicRatioLimit> ratioLimits = PursuitConfig.ratioLimits();
    assertEquals(ratioLimits.size(), 1);
  }
}
