// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class ComputerSensorsEventTest extends TestCase {
  public void testSimple() {
    byte[] array = new byte[] { 0, 1, 0, 3, 60, 67, 64 };
    ComputerSensorsEvent cse = new ComputerSensorsEvent(ByteBuffer.wrap(array));
    // TODO
    // assertEquals(cse.getTemperatureMax(), Quantity.of(67, SI.DEGREE_CELSIUS));
  }
}
