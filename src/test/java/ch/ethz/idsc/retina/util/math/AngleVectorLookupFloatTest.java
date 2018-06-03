// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
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

  public void testSimpleOffset() {
    AngleVectorLookupFloat tlf = new AngleVectorLookupFloat(360, true, Math.PI / 2);
    float dx = tlf.dx(90);
    float dy = tlf.dy(90);
    assertEquals(Tensors.vector(dx, dy), UnitVector.of(2, 0));
  }
}
