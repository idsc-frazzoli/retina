// code by jph
package ch.ethz.idsc.retina.util.math;

import java.awt.Point;
import java.util.Optional;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ViewportTest extends TestCase {
  public void testToPixel() {
    Viewport viewport = Viewport.create(100, 80);
    Optional<Point> optional = viewport.toPixel(Tensors.vector(0, 0, 3, .9));
    Point point = optional.get();
    assertEquals(point.x, 50);
    assertEquals(point.y, 40);
  }
}
