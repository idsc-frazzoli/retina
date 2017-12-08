// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import junit.framework.TestCase;

public class MiscEmergencyModuleTest extends TestCase {
  public void testNominal() throws Exception {
    MiscEmergencyModule miscEmergencyModule = new MiscEmergencyModule();
    miscEmergencyModule.first();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put((byte) 0);
    byteBuffer.putFloat(12.0f);
    byteBuffer.flip();
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    miscEmergencyModule.getEvent(miscGetEvent);
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }

  public void testEmergency() throws Exception {
    MiscEmergencyModule miscEmergencyModule = new MiscEmergencyModule();
    miscEmergencyModule.first();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put((byte) 1);
    byteBuffer.putFloat(12.0f);
    byteBuffer.flip();
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    miscEmergencyModule.getEvent(miscGetEvent);
    assertTrue(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }

  public void testLowPower() throws Exception {
    MiscEmergencyModule miscEmergencyModule = new MiscEmergencyModule();
    miscEmergencyModule.first();
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put((byte) 0);
    byteBuffer.putFloat(10.3f);
    byteBuffer.flip();
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    miscEmergencyModule.getEvent(miscGetEvent);
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    Thread.sleep(1100); // timeout increased to 1[s]
    assertTrue(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }
}
