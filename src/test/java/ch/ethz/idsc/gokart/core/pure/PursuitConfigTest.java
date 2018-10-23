// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class PursuitConfigTest extends TestCase {
  public void testSimple() {
    assertFalse(PursuitConfig.GLOBAL.isQualitySufficient(RealScalar.ZERO));
    assertTrue(PursuitConfig.GLOBAL.isQualitySufficient(RealScalar.ONE));
  }
}
