// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MPCOptimizationParameterKinematicTest extends TestCase {
  public void testBasic() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[7 * 4]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putFloat(1.5f);
    byteBuffer.putFloat(2.5f);
    byteBuffer.position(0);
    MPCOptimizationParameterKinematic mpcOptimizationParameterKinematic = //
        new MPCOptimizationParameterKinematic(byteBuffer);
    assertEquals(mpcOptimizationParameterKinematic.speedLimit(), Quantity.of(1.5, SI.VELOCITY));
    assertEquals(mpcOptimizationParameterKinematic.xAccLimit(), Quantity.of(2.5, SI.ACCELERATION));
  }

  public void testSimple() throws Exception {
    // only sends a simple message
    // uncomment if you are able to compile the binary
    /* LcmMPCPathFollowingClient lcmMPCPathFollowingClient = new LcmMPCPathFollowingClient();
     * try {
     * lcmMPCPathFollowingClient.start();
     * for (int i = 0; i < 4; i++) {
     * System.out.println("i=" + i);
     * GokartState gokartState = new GokartState(//
     * 11, //
     * 12, //
     * 13, //
     * 14, //
     * 15, //
     * 16, //
     * 17, //
     * 18, //
     * 19, //
     * 20);
     * lcmMPCPathFollowingClient.publishGokartState(gokartState);
     * Thread.sleep(1000);
     * // System.out.print(lcmMPCPathFollowingClient.mpcNativeSession.getNativeOutput());
     * }
     * lcmMPCPathFollowingClient.stop();
     * } catch (Exception exception) {
     * exception.printStackTrace();
     * } */
  }

  public void testRealBinary() throws Exception {
    MPCRequestPublisher mpcRequestPublisher = MPCRequestPublisher.kinematic();
    MPCControlUpdateLcmClient mpcControlUpdateLcmClient = new MPCControlUpdateLcmClient();
    mpcControlUpdateLcmClient.startSubscriptions();
    // start binary via command line
    // uncomment if you want to start the server yourself (useful if you want to see output)
    // lcmMPCControlClient.switchToExternalStart();
    try {
      // mpcRequestPublisher.start(); // TODO MPC start (external) process somewhere
      GokartState gokartState = new GokartState(//
          11, //
          1, //
          0, //
          0, //
          36.2f, //
          37.7f, //
          0, //
          0, //
          0, //
          0, 60);
      MPCOptimizationParameterKinematic optimizationParameter = TestHelper.optimizationParameterKinematic( //
          Quantity.of(10, SI.VELOCITY), Quantity.of(5, SI.ACCELERATION), Quantity.of(5, SI.ACCELERATION));
      mpcRequestPublisher.publishOptimizationParameter(optimizationParameter);
      MPCControlUpdateCapture mpcControlUpdateCapture = new MPCControlUpdateCapture();
      mpcControlUpdateLcmClient.addListener(mpcControlUpdateCapture);
      MPCBSplineTrack track = DubendorfTrack.HYPERLOOP_EIGHT;
      Tensor position = gokartState.getPositionXY();
      MPCPathParameter mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINE_PREVIEW_SIZE, position, Quantity.of(0, SI.METER));
      mpcRequestPublisher.publishControlRequest(gokartState, mpcPathParameter);
      Thread.sleep(100);// should even work with 30ms
      System.out.println(mpcControlUpdateCapture.cns);
      // TODO MPC reinstate on a PC with the binaries
      // assertNotNull(mpcControlUpdateCapture.cns);
      // mpcRequestPublisher.stop(); // TODO MPC stop (external) process somewhere
      mpcControlUpdateLcmClient.stopSubscriptions();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
