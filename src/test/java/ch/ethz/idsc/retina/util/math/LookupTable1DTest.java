// code by jph
package ch.ethz.idsc.retina.util.math;

import java.io.IOException;

import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class LookupTable1DTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    LookupTable1D lookupTable1D = Serialization.copy(new LookupTable1D(Subdivide.of(0, 1, 4)));
    assertEquals(lookupTable1D.at(1), 0.25f);
    assertEquals(lookupTable1D.at(2), 0.5f);
    assertEquals(lookupTable1D.at(4), 1f);
  }

  public void testFailMatrix() {
    try {
      new LookupTable1D(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
