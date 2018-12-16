// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerComponentTest extends TestCase {
  public void testSimple() {
    SteerComponent steerComponent = new SteerComponent();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[44]);
    SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
    steerComponent.getEvent(steerGetEvent);
    Optional<SteerPutEvent> optional = steerComponent.putEvent();
    assertFalse(optional.isPresent()); // not calibrated
    steerComponent.putEvent(SteerPutEvent.createOn(Quantity.of(1.3f, "SCT")));
  }
}
