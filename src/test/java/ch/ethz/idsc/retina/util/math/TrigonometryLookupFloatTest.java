// code by jph
package ch.ethz.idsc.retina.util.math;

import junit.framework.TestCase;

public class TrigonometryLookupFloatTest extends TestCase {
  public void testSimple() {
    TrigonometryLookupFloat tlf = new TrigonometryLookupFloat(360, false);
    float dx = tlf.dx(20);
    float dy = tlf.dy(20);
    assertEquals(dx, (float) Math.cos(20 * 2 * Math.PI / 360));
    assertEquals(dy, (float) Math.sin(20 * 2 * Math.PI / 360));
  }

  public void testSimpleFlip() {
    TrigonometryLookupFloat tlf = new TrigonometryLookupFloat(360, true);
    float dx = tlf.dx(20);
    float dy = tlf.dy(20);
    assertEquals(dx, (float) Math.cos(20 * 2 * Math.PI / 360));
    assertEquals(dy, (float) -Math.sin(20 * 2 * Math.PI / 360));
  }
}
