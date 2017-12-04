// code by jph
package ch.ethz.idsc.retina.gui.gokart.lab;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import junit.framework.TestCase;

public class LinmotComponentTest extends TestCase {
  public void testSimple() {
    LinmotComponent linmotComponent = new LinmotComponent();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    linmotComponent.getEvent(linmotGetEvent);
    Optional<LinmotPutEvent> optional = linmotComponent.putEvent();
    assertTrue(optional.isPresent());
    LinmotPutEvent linmotPutEvent = optional.get();
    linmotComponent.putEvent(linmotPutEvent);
  }
}
