// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PerSectionModuleTest extends TestCase {
  public void testSimple() {
    SpeedLimitPerSectionModule perSectionModule = new SpeedLimitPerSectionModule();
    perSectionModule.first();
    assertFalse(perSectionModule.putEvent().isPresent());
    perSectionModule.last();
  }

  public void testSimple1() {
    SpeedLimitPerSectionModule perSectionModule = new SpeedLimitPerSectionModule();
    perSectionModule.first();
    perSectionModule.putEvent();
    perSectionModule.last();
  }

  public void testSimple3() {
    SpeedLimitPerSectionModule perSectionModule = new SpeedLimitPerSectionModule();
    perSectionModule.first();
    Tensor pose = Tensors.of(//
        Quantity.of(0, SI.METER), //
        Quantity.of(0, SI.METER), //
        RealScalar.of(0));
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(5000, 5000);
    GokartPoseEvent testEvent = GokartPoseEvents.create(pose, RealScalar.ONE);
    perSectionModule.getEvent(testEvent);
    perSectionModule.rimoGetListener.getEvent(rimoGetEvent);
    RimoPutEvent rimoPutEvent = perSectionModule.putEvent().get();
    System.out.println(rimoPutEvent.getTorque_Y_pair());
    perSectionModule.last();
  }
}
