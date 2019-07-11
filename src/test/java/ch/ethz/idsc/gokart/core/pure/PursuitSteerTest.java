// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PursuitSteerTest extends TestCase {
  public void testRanks() {
    assertEquals(new PursuitSteer().getProviderRank(), ProviderRank.AUTONOMOUS);
    assertEquals(new PursuitRimo().getProviderRank(), ProviderRank.AUTONOMOUS);
  }

  public void testControl() {
    PursuitSteer pps = new PursuitSteer();
    Optional<SteerPutEvent> optional;
    optional = pps.private_putEvent(new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
    CurvePurePursuitModuleTest._checkFallback(optional);
    optional = pps.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
    assertTrue(optional.isPresent());
    pps.setRatio(Quantity.of(-0.2, SI.PER_METER));
    pps.control(new SteerColumnAdapter(true, Quantity.of(0.2, "SCE")));
    pps.setRatio(Quantity.of(-0.1, SI.PER_METER));
    pps.control(new SteerColumnAdapter(true, Quantity.of(0.1, "SCE")));
  }

  public void testNotCalibrated2() {
    PursuitSteer pps = new PursuitSteer();
    pps.setOperational(true);
    CurvePurePursuitModuleTest._checkFallback(pps.putEvent());
    Optional<SteerPutEvent> optional;
    optional = pps.private_putEvent(new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
    CurvePurePursuitModuleTest._checkFallback(optional);
  }
}
