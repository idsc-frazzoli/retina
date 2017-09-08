// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;

import junit.framework.TestCase;

public class UserHomeTest extends TestCase {
  public void testSimple() {
    File file = UserHome.file("");
    assertTrue(file.isDirectory());
  }

  public void testNull() {
    try {
      UserHome.file(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
