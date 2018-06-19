// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.File;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    assertEquals(StaticHelper.load(new File("does_not_exist")), null);
  }

  public void testBoolean() {
    assertEquals(Boolean.TRUE.toString(), "true");
    assertEquals(Boolean.FALSE.toString(), "false");
  }
}
