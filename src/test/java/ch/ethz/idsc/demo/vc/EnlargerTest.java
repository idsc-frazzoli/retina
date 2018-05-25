package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EnlargerTest extends TestCase {
  public void testSimple() {
    Tensor p = Tensors.fromString("{{{0,0},{0,1},{1,1},{1,0}}, {{0,0},{0,1},{0.5,0.5}}}");
    Enlarger test = new Enlarger(p);
    assertTrue(test.getTotalArea() == 1.25);
    Tensor clip = Tensors.fromString("{{0,0},{1,0},{1,1},{0,1}}");
    Tensor subj = Tensors.fromString("{{0,0},{0,1},{0.5,0.5}}");
    System.out.println(PolygonIntersecter.PolygonIntersect(clip, subj));
  }
}
