// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import junit.framework.TestCase;

public class LinmotEmergencyModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    LinmotEmergencyModule linmotEmergencyModule = new LinmotEmergencyModule();
    linmotEmergencyModule.first();
    linmotEmergencyModule.last();
  }

  public void testTimeout() throws Exception {
    LinmotEmergencyModule linmotEmergencyModule = new LinmotEmergencyModule();
    // linmotEmergencyModule.first();
    assertFalse(linmotEmergencyModule.putEvent().isPresent());
    Thread.sleep(70);
    assertTrue(linmotEmergencyModule.putEvent().isPresent()); // timeout
  }

  public void testOperational() throws Exception {
    LinmotEmergencyModule linmotEmergencyModule = new LinmotEmergencyModule();
    // linmotEmergencyModule.first();
    assertFalse(linmotEmergencyModule.putEvent().isPresent());
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    linmotEmergencyModule.getEvent(linmotGetEvent);
    assertTrue(linmotEmergencyModule.putEvent().isPresent()); // not operational
  }
}
