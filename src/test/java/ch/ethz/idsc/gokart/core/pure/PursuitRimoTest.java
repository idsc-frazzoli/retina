// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PursuitRimoTest extends TestCase {
  public void testNotCalibrated() {
    PursuitRimo pps = new PursuitRimo();
    assertFalse(pps.putEvent().isPresent());
    Optional<RimoPutEvent> optional = //
        pps.private_putEvent(new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
    assertFalse(optional.isPresent());
  }

  public void testNotCalibrated2() {
    PursuitRimo pps = new PursuitRimo();
    pps.setOperational(true);
    assertFalse(pps.putEvent().isPresent());
    Optional<RimoPutEvent> optional = //
        pps.private_putEvent(new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
    assertFalse(optional.isPresent());
  }

  public void testMinor() {
    PursuitRimo ppr = new PursuitRimo();
    ppr.setOperational(true);
    assertFalse(ppr.putEvent().isPresent());
  }

  public void testSimple() {
    PursuitRimo ppr = new PursuitRimo();
    assertEquals(ppr.getSpeed(), Quantity.of(0.0, SI.PER_SECOND));
    assertFalse(ppr.putEvent().isPresent());
    {
      Optional<RimoPutEvent> optional = ppr.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent()); // because speed reading is missing
    }
    RimoGetEvent rge = RimoGetEvents.create(340, 320);
    ppr.setSpeed(PurePursuitConfig.GLOBAL.rateFollower);
    assertEquals(ppr.getSpeed(), PurePursuitConfig.GLOBAL.rateFollower);
    ppr.rimoRateControllerWrap.getEvent(rge);
    {
      Optional<RimoPutEvent> optional = ppr.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertTrue(optional.isPresent());
      RimoPutEvent rpe = optional.get();
      short trqL = rpe.putTireL.getTorqueRaw();
      short trqR = rpe.putTireR.getTorqueRaw();
      assertTrue(trqL < 0);
      assertTrue(0 < trqR);
    }
  }

  public void testSlowdown() {
    PursuitRimo ppr = new PursuitRimo();
    assertEquals(ppr.getSpeed(), Quantity.of(0.0, SI.PER_SECOND));
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
      short trqL = rpe.putTireL.getTorqueRaw();
      short trqR = rpe.putTireR.getTorqueRaw();
      assertTrue(trqL > 0);
      assertTrue(0 > trqR);
    }
  }

  public void testSimpleBranch() {
    PursuitRimo ppr = new PursuitRimo();
    assertEquals(ppr.getSpeed(), Quantity.of(0.0, SI.PER_SECOND));
    assertFalse(ppr.putEvent().isPresent());
    {
      Optional<RimoPutEvent> optional = ppr.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent()); // because speed reading is missing
    }
    {
      Optional<RimoPutEvent> optional = ppr.private_putEvent(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent());
    }
    ppr.rimoRateControllerWrap.getEvent(RimoGetEvents.create(123, 234));
    {
      Optional<RimoPutEvent> optional = ppr.private_putEvent(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent());
    }
    ppr.setOperational(true);
    {
      Optional<RimoPutEvent> optional = ppr.private_putEvent(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertTrue(optional.isPresent());
    }
    {
      Optional<RimoPutEvent> optional = ppr.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertTrue(optional.isPresent());
    }
  }
}
