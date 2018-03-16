// code by jph
package ch.ethz.idsc.retina.util.math;

import junit.framework.TestCase;

public class AngleVectorLookupFloatTest extends TestCase {
  public void testSimple() {
    AngleVectorLookupFloat tlf = new AngleVectorLookupFloat(360, false, 0);
    float dx = tlf.dx(20);
    float dy = tlf.dy(20);
    assertEquals(dx, (float) Math.cos(20 * 2 * Math.PI / 360));
    assertEquals(dy, (float) Math.sin(20 * 2 * Math.PI / 360));
  }

  public void testSimpleFlip() {
    AngleVectorLookupFloat tlf = new AngleVectorLookupFloat(360, true, 0);
    float dx = tlf.dx(20);
    float dy = tlf.dy(20);
    assertEquals(dx, (float) Math.cos(20 * 2 * Math.PI / 360));
    assertEquals(dy, (float) -Math.sin(20 * 2 * Math.PI / 360));
  }
}
