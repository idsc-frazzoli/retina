// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.gokart.core.OvalTrack;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;
import idsc.BinaryBlob;
import junit.framework.TestCase;

public class Se2CurveLcmTest extends TestCase {
  public void testSimple() {
    BinaryBlob binaryBlob = Se2CurveLcm.encode(OvalTrack.SE2);
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    Tensor tensor = Se2CurveLcm.decode(byteBuffer);
    Chop._05.requireClose(tensor, OvalTrack.SE2);
  }
}
