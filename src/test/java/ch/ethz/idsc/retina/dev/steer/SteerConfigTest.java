// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.Units;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SteerConfigTest extends TestCase {
  public void testSimple() {
    Scalar q = Quantity.of(2, "km*NOU");
    Scalar r = QuantityMagnitude.SI().in(Unit.of("m*NOU")).apply(q);
    assertEquals(r, RealScalar.of(2000));
  }

  public void testSCE() {
    assertEquals(Units.of(SteerConfig.GLOBAL.columnMax), Unit.of("SCE"));
  }

  public void testSCEfromAngle() {
    Scalar q = SteerConfig.GLOBAL.getSCEfromAngle(Quantity.of(1, "rad"));
    assertEquals(Units.of(q), Unit.of("SCE"));
    assertTrue(1.1 < q.number().doubleValue());
  }

  public void testAngleLimit() {
    Clip clip = SteerConfig.GLOBAL.getAngleLimit();
    assertEquals(clip.min(), clip.max().negate());
  }
}
