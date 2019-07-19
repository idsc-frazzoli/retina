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
    PursuitRimo pursuitRimo = new PursuitRimo();
    assertFalse(pursuitRimo.putEvent().isPresent());
    Optional<RimoPutEvent> optional = //
        pursuitRimo.private_putEvent(new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
    assertFalse(optional.isPresent());
  }

  public void testNotCalibrated2() {
    PursuitRimo pursuitRimo = new PursuitRimo();
    pursuitRimo.setOperational(true);
    assertFalse(pursuitRimo.putEvent().isPresent());
    Optional<RimoPutEvent> optional = //
        pursuitRimo.private_putEvent(new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
    assertFalse(optional.isPresent());
  }

  public void testMinor() {
    PursuitRimo pursuitRimo = new PursuitRimo();
    pursuitRimo.setOperational(true);
    assertFalse(pursuitRimo.putEvent().isPresent());
  }

  public void testSimple() {
    PursuitRimo pursuitRimo = new PursuitRimo();
    assertEquals(pursuitRimo.getSpeed(), Quantity.of(0.0, SI.PER_SECOND));
    assertFalse(pursuitRimo.putEvent().isPresent());
    {
      Optional<RimoPutEvent> optional = pursuitRimo.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent()); // because speed reading is missing
    }
    RimoGetEvent rge = RimoGetEvents.create(340, 320);
    pursuitRimo.setSpeed(PurePursuitConfig.GLOBAL.rateFollower);
    assertEquals(pursuitRimo.getSpeed(), PurePursuitConfig.GLOBAL.rateFollower);
    pursuitRimo.rimoRateControllerWrap.getEvent(rge);
    {
      Optional<RimoPutEvent> optional = pursuitRimo.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertTrue(optional.isPresent());
      RimoPutEvent rpe = optional.get();
      short trqL = rpe.putTireL.getTorqueRaw();
      short trqR = rpe.putTireR.getTorqueRaw();
      assertTrue(trqL < 0);
      assertTrue(0 < trqR);
    }
  }

  public void testSlowdown() {
    PursuitRimo pursuitRimo = new PursuitRimo();
    assertEquals(pursuitRimo.getSpeed(), Quantity.of(0.0, SI.PER_SECOND));
    assertFalse(pursuitRimo.putEvent().isPresent());
    {
      Optional<RimoPutEvent> optional = pursuitRimo.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent()); // because speed reading is missing
    }
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(340, 320);
    pursuitRimo.rimoRateControllerWrap.getEvent(rimoGetEvent);
    {
      Optional<RimoPutEvent> optional = pursuitRimo.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertTrue(optional.isPresent());
      RimoPutEvent rimoPutEvent = optional.get();
      short trqL = rimoPutEvent.putTireL.getTorqueRaw();
      short trqR = rimoPutEvent.putTireR.getTorqueRaw();
      assertTrue(trqL > 0);
      assertTrue(0 > trqR);
    }
  }

  public void testSimpleBranch() {
    PursuitRimo pursuitRimo = new PursuitRimo();
    assertEquals(pursuitRimo.getSpeed(), Quantity.of(0.0, SI.PER_SECOND));
    assertFalse(pursuitRimo.putEvent().isPresent());
    {
      Optional<RimoPutEvent> optional = pursuitRimo.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent()); // because speed reading is missing
    }
    {
      Optional<RimoPutEvent> optional = pursuitRimo.private_putEvent(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent());
    }
    pursuitRimo.rimoRateControllerWrap.getEvent(RimoGetEvents.create(123, 234));
    {
      Optional<RimoPutEvent> optional = pursuitRimo.private_putEvent(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent());
    }
    pursuitRimo.setOperational(true);
    {
      Optional<RimoPutEvent> optional = pursuitRimo.private_putEvent(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertTrue(optional.isPresent());
    }
    {
      Optional<RimoPutEvent> optional = pursuitRimo.control(new SteerColumnAdapter(true, Quantity.of(0.3, "SCE")));
      assertTrue(optional.isPresent());
    }
  }
}
