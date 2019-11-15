package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import junit.framework.TestCase;

public class PacejkaPowerSteeringTest extends TestCase {
  public void testSimple() {
    HapticSteerConfig hapticSteerConfig = new HapticSteerConfig();
    // hapticSteerConfig.pacejkaB3 = RealScalar.of(3.4);
    PacejkaPowerSteering pps = new PacejkaPowerSteering(hapticSteerConfig);
    Scalar term1 = pps.term1(Quantity.of(0.1, "SCE"), Tensors.fromString("{4[m*s^-1],2[m*s^-1],1[s^-1] }"));
    System.out.println(UnitSystem.SI().apply(term1));
    System.out.println(term1);
  }
}
