// code by jph
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    int filterCount = StaticHelper.filterCount(new boolean[] { true, false, true, true, false });
    assertEquals(filterCount, 3);
  }
}
