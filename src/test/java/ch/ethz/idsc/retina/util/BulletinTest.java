// code by jph
package ch.ethz.idsc.retina.util;

import junit.framework.TestCase;

public class BulletinTest extends TestCase {
  public void testSimple() {
    Bulletin bulletin = new Bulletin();
    bulletin.append("first");
    bulletin.append("test\nblub");
    assertEquals(bulletin.size(), 3);
  }
}
