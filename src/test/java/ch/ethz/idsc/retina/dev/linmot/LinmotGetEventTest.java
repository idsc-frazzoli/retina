// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class LinmotGetEventTest extends TestCase {
  public void testLength() {
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(ByteBuffer.wrap(new byte[16]));
    assertEquals(linmotGetEvent.length(), 16);
    linmotGetEvent.asArray();
  }
}
