// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class RimoComponentTest extends TestCase {
  public void testSimple() {
    RimoComponent rimoComponent = new RimoComponent();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);
    rimoComponent.getEvent(rimoGetEvent);
    Optional<RimoPutEvent> optional = rimoComponent.putEvent();
    assertTrue(optional.isPresent());
    rimoComponent.putEvent(optional.get());
  }
}
