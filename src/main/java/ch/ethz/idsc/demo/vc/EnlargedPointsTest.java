package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EnlargedPointsTest extends TestCase {
  public void testSimple() {
    Tensor p = Tensors.fromString("{{{0,0},{0,1},{1,1},{1,0}}, {{0,0},{0,1},{0.5,0.5}}}");
    EnlargedPoints test = new EnlargedPoints(p);
    assertTrue(test.getTotalArea() == 1.25);
    
    Tensor clip=Tensors.fromString("{{0,0},{0,1},{1,1},{1,0}}");
    Tensor subj=Tensors.fromString("{{0,0},{1,0},{0.5,0.5}}");
    PolygonIntersecter.PolygonIntersect(clip, subj);
    ;
  }
}
