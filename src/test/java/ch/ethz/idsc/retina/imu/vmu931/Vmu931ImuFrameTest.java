// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Vmu931ImuFrameTest extends TestCase {
  public void testSimple() {
    Tensor in_acc = Tensors.fromString("{9.81[m*s^-2], 1[m*s^-2], -9.81[m*s^-2]}");
    Tensor in_gyr = Tensors.fromString("{1[s^-1], 2[s^-1], 3[s^-1]}");
    Vmu931ImuFrame vmu931ImuFrame = Vmu931ImuFrames.create(123, in_acc, in_gyr);
    assertEquals(vmu931ImuFrame.timestamp_ms(), 123);
    Chop._06.requireClose(vmu931ImuFrame.acceleration(), in_acc);
    Chop._05.requireClose(vmu931ImuFrame.gyroscope(), in_gyr);
  }
}
