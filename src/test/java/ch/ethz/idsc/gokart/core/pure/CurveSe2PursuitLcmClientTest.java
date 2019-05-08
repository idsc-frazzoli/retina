// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.gokart.lcm.mod.Se2CurveLcm;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ref.TensorListener;
import ch.ethz.idsc.tensor.sca.Chop;
import idsc.BinaryBlob;
import junit.framework.TestCase;

public class CurveSe2PursuitLcmClientTest extends TestCase {
  int count = 0;

  public void testSimple() {
    CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
    curveSe2PursuitLcmClient.addListener(new TensorListener() {
      @Override
      public void tensorReceived(Tensor tensor) {
        Chop._04.requireClose(tensor, DubendorfCurve.TRACK_OVAL_SE2);
        ++count;
      }
    });
    curveSe2PursuitLcmClient.startSubscriptions();
    BinaryBlob binaryBlob = Se2CurveLcm.encode(DubendorfCurve.TRACK_OVAL_SE2);
    curveSe2PursuitLcmClient.messageReceived(ByteBuffer.wrap(binaryBlob.data).order(ByteOrder.LITTLE_ENDIAN));
    curveSe2PursuitLcmClient.stopSubscriptions();
    assertEquals(count, 1);
  }
}
