// code by vc, jph
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AreaMeasureTest extends TestCase {
  private static final Chop CHOP = Chop.below(0.05);

  public void testRectangles() {
    Tensor hulls = Tensors.fromString("{ {{0,0},{1,0},{1,1},{0,1}}, {{2,0},{3,0},{3,2},{2,2}}}");
    EnlargedPoints test = new EnlargedPoints(hulls);
    double area = AreaMeasure.of(test.getArea());
    assertEquals(area, 3.0);
  }

  public void testPyramid() {
    Tensor hulls = Tensors.fromString("{{{0,0},{1,0},{0.5,1}}}");
    EnlargedPoints test1 = new EnlargedPoints(hulls);
    double area = AreaMeasure.of(test1.getArea());
    CHOP.requireClose(DoubleScalar.of(area), RationalScalar.HALF);
  }

  public void testSimple() {
    Tensor hulls = Tensors.fromString("{ {{-1,0},{0,2},{1,0},{0,-2}}, {{2,0},{3,0},{3,2},{0,2}}}");
    EnlargedPoints test2 = new EnlargedPoints(hulls);
    double area = AreaMeasure.of(test2.getArea());
    CHOP.requireClose(DoubleScalar.of(area), RealScalar.of(8));
  }

  public void testMidPointRectangles() {
    Tensor hulls = Tensors.fromString("{ {{0,0},{1,0},{1,1},{0,1}}, {{2,0},{3,0},{3,2},{2,2}}}");
    EnlargedPoints test = new EnlargedPoints(hulls);
    double area = AreaMeasure.midpoint(test.getArea());
    assertEquals(area, 3.0);
  }

  public void testMidPointPyramid() {
    Tensor hull = Tensors.fromString("{{0,0},{1,0},{0.5,1}}");
    double area = AreaMeasure.midpoint(EnlargedPoints.toArea(hull));
    assertEquals(area, 0.5);
  }

  public void testMidPointPyramidShifted() {
    Tensor hull = Tensors.fromString("{{0+2,0+5},{1+2,0+5},{0.5+2,1+5}}");
    double area = AreaMeasure.midpoint(EnlargedPoints.toArea(hull));
    assertEquals(area, 0.5);
  }

  public void testMidPointSimple() {
    Tensor hulls = Tensors.fromString("{ {{-1,0},{0,2},{1,0},{0,-2}}, {{2,0},{3,0},{3,2},{0,2}} }");
    EnlargedPoints test2 = new EnlargedPoints(hulls);
    double area = AreaMeasure.midpoint(test2.getArea());
    CHOP.requireClose(DoubleScalar.of(area), RealScalar.of(8));
  }
}
