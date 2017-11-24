// code by jph
package ch.ethz.idsc.retina.util.math;

import java.awt.Point;
import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ProjectionMatrixTest extends TestCase {
  public void testSimple() {
    // ProjectionMatrix.perspective(1.1, dimension.width / (double) dimension.height, 1, 100);
    Viewport viewport = Viewport.create(240, 180);
    Tensor project = ProjectionMatrix.of(RealScalar.of(1.1), viewport.aspectRatio(), Clip.function(1, 100));
    // Optional<Tensor> optional = ;
    assertFalse(viewport.fromProjected(project.dot(Tensors.vector(-1, -1, 0, 1))).isPresent());
    assertFalse(viewport.fromProjected(project.dot(Tensors.vector(-1, -1, 1, 1))).isPresent());
    Optional<Tensor> optional = viewport.fromProjected(project.dot(Tensors.vector(-2, -1, -2, 1)));
    assertTrue(optional.isPresent());
    // System.out.println(optional.get());
  }

  public void testToPixel() {
    Viewport viewport = Viewport.create(240, 180);
    Tensor project = ProjectionMatrix.of(RealScalar.of(1.1), viewport.aspectRatio(), Clip.function(1, 100));
    assertFalse(viewport.toPixel(project.dot(Tensors.vector(-20, -1, -10, 1))).isPresent());
    Optional<Point> optional = viewport.toPixel(project.dot(Tensors.vector(-2, -1, -10, 1)));
    assertTrue(optional.isPresent());
  }

  public void testToPixel1() {
    Viewport viewport = Viewport.create(240, 180);
    Tensor project = ProjectionMatrix.of(RealScalar.of(1.1), viewport.aspectRatio(), Clip.function(1, 100));
    // assertFalse(viewport.toPixel(project.dot(Tensors.vector(-20, -1, -10, 1))).isPresent());
    {
      Optional<Point> optional = viewport.toPixel(project.dot(Tensors.vector(-2, -1, -10, 1)));
      // System.out.println(optional.get());
      assertTrue(optional.isPresent());
    }
    {
      Optional<Point> optional = viewport.toPixel(project.dot(Tensors.vector(+2, -1, -10, 1)));
      // System.out.println(optional.get());
      assertTrue(optional.isPresent());
    }
    {
      Optional<Point> optional = viewport.toPixel(project.dot(Tensors.vector(+2, +1, -10, 1)));
      // System.out.println(optional.get());
      assertTrue(optional.isPresent());
    }
    {
      Optional<Point> optional = viewport.toPixel(project.dot(Tensors.vector(0, 0, -10, 1)));
      // System.out.println(optional.get());
      assertEquals(optional.get(), new Point(120, 90));
      assertTrue(optional.isPresent());
    }
  }

  public void testFail() {
    ProjectionMatrix.of(1.1, 240 / 180.0, 1, 100);
  }
}
