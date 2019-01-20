// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class TrackLayoutInitialGuessTest extends TestCase {
  public void testSimple() {
    System.out.println(Scalars.compare(RealScalar.ZERO, RealScalar.ONE));
  }
}
