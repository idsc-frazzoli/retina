// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EnlargedPointsTest extends TestCase {
  public void testSimple() {
    Tensor p = Tensors.fromString("{ {{0,0},{1,0},{1,1},{0,1}}, {{2,0},{3,0},{3,2},{2,2}}}");
    EnlargedPoints test = new EnlargedPoints(p);
    System.out.println(EnlargedPoints.areaCalculator(test.getArea())); // expected:3
    Tensor p1 = Tensors.fromString("{{{0,0},{1,0},{0.5,1}}}");
    EnlargedPoints test1 = new EnlargedPoints(p1);
    System.out.println(EnlargedPoints.areaCalculator(test1.getArea())); // expected 0.5
    Tensor p2 = Tensors.fromString("{ {{-1,0},{0,2},{1,0},{0,-2}}, {{2,0},{3,0},{3,2},{0,2}}}");
    EnlargedPoints test2 = new EnlargedPoints(p2);
    System.out.println(EnlargedPoints.areaCalculator(test2.getArea())); // expected:8
  }
}
