// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.dev.misc.MiscGetEvent;
import ch.ethz.idsc.gokart.dev.misc.MiscPutEvent;
import junit.framework.TestCase;

public class MiscComponentTest extends TestCase {
  public void testSimple() {
    MiscComponent miscComponent = new MiscComponent();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    miscComponent.getEvent(miscGetEvent);
    Optional<MiscPutEvent> optional = miscComponent.putEvent();
    assertTrue(optional.isPresent());
    miscComponent.putEvent(optional.get());
  }
}
