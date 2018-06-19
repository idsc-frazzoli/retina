// code by jph
package ch.ethz.idsc.gokart.gui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class GokartStatusEventTest extends TestCase {
  public void testSimple() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(Float.NaN);
    assertFalse(gokartStatusEvent.isSteerColumnCalibrated());
    try {
      SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testUnitless() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0.1f);
    assertTrue(gokartStatusEvent.isSteerColumnCalibrated());
    Scalar scalar = SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent);
    assertFalse(scalar instanceof Quantity);
    Clip.function(0.05, 0.08).requireInside(scalar);
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    gokartStatusEvent.insert(byteBuffer);
    byteBuffer.flip();
    assertEquals(byteBuffer.getFloat(), 0.1f);
  }

  public void testBufferOk() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putFloat(0.1f);
    byteBuffer.flip();
    GokartStatusEvent gse = new GokartStatusEvent(byteBuffer);
    assertTrue(gse.isSteerColumnCalibrated());
  }

  public void testBufferNaN() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putFloat(Float.NaN);
    byteBuffer.flip();
    GokartStatusEvent gse = new GokartStatusEvent(byteBuffer);
    assertFalse(gse.isSteerColumnCalibrated());
    assertEquals(gse.length(), 4);
  }
}
