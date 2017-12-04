// code by jph
package ch.ethz.idsc.retina.gui.gokart.lab;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class RimoComponentTest extends TestCase {
  public void testSimple() {
    RimoComponent rimoComponent = new RimoComponent();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
    rimoComponent.getEvent(rimoGetEvent);
    Optional<RimoPutEvent> optional = rimoComponent.putEvent();
    assertTrue(optional.isPresent());
    rimoComponent.putEvent(optional.get());
  }
}
