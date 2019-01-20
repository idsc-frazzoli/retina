// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ChirpSignalTest extends TestCase {
  public void testSimple() {
    ChirpSignal chirpSignal = new ChirpSignal(1, 1, 10);
    Scalar value = chirpSignal.apply(RealScalar.of(2.34));
    assertTrue(Chop._12.close(value, RealScalar.of(0.46959484095013415)));
  }
}
