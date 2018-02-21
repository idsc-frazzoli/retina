// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.PurePursuitRimo;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PurePursuitRimoTest extends TestCase {
  public void testNotCalibrated() {
    PurePursuitRimo ppr = new PurePursuitRimo();
    assertFalse(ppr.putEvent().isPresent());
    Optional<RimoPutEvent> optional = ppr.control(new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
    assertFalse(optional.isPresent());
  }

  public void testMinor() {
    PurePursuitRimo ppr = new PurePursuitRimo();
    ppr.setOperational(true);
    assertFalse(ppr.putEvent().isPresent());
  }

  public void testSimple() {
    PurePursuitRimo ppr = new PurePursuitRimo();
    assertFalse(ppr.putEvent().isPresent());
    {
      Optional<RimoPutEvent> optional = ppr.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent()); // because speed reading is missing
    }
    RimoGetEvent rge = RimoGetEvents.create(340, 320);
    ppr.rimoRateControllerWrap.getEvent(rge);
    {
      Optional<RimoPutEvent> optional = ppr.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertTrue(optional.isPresent());
      RimoPutEvent rpe = optional.get();
      short trqL = rpe.putL.getTorqueRaw();
      short trqR = rpe.putR.getTorqueRaw();
      assertTrue(trqL < 0);
      assertTrue(0 < trqR);
    }
  }
}
