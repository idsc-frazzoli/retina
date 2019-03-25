// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import junit.framework.TestCase;

public class Vmu931Test extends TestCase {
  public void testSimple() {
    new Vmu931("/dev/ttyUSB1", //
        EnumSet.of(Vmu931Channel.ACCELEROMETER, Vmu931Channel.GYROSCOPE), //
        Vmu931_DPS._250, Vmu931_G._16, new Vmu931Listener() {
          @Override
          public void gyroscope(ByteBuffer byteBuffer) {
            // ---
          }

          @Override
          public void accelerometer(ByteBuffer byteBuffer) {
            // ---
          }
        });
  }
}
