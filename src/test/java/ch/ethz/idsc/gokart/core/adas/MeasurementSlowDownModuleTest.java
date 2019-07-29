// code by jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class MeasurementSlowDownModuleTest extends TestCase {
  public void testSimple() {
    MeasurementSlowDownModule slowDown = new MeasurementSlowDownModule();
    slowDown.first();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(5000, 5000);
    slowDown.rimoGetListener.getEvent(rimoGetEvent);
    RimoPutEvent rimoPutEvent = slowDown.putEvent().get();
    System.out.println(rimoPutEvent.getTorque_Y_pair());
    slowDown.last();
    System.out.println(" ");
  }
}
