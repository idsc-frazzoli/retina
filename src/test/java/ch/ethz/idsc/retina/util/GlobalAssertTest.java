// code by jph
package ch.ethz.idsc.retina.util;

import ch.ethz.idsc.owl.data.GlobalAssert;
import junit.framework.TestCase;

public class GlobalAssertTest extends TestCase {
  public void testSimple() {
    try {
      GlobalAssert.that(false);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
