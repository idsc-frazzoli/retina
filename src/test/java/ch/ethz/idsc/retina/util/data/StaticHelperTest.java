// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testBoolean() {
    assertEquals(Boolean.TRUE.toString(), "true");
    assertEquals(Boolean.FALSE.toString(), "false");
  }
}
