// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MPCPathFollowingClientLCMTest extends TestCase {
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
      // TODO test implementation not universal. requires binary?
      mpcRequestPublisher.start();
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
      DubendorfTrack track = DubendorfTrack.HYPERLOOP_EIGHT;
      Tensor position = Tensors.of(gokartState.getX(), gokartState.getY());
      MPCPathParameter mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINE_PREVIEW_SIZE, position, Quantity.of(0, SI.METER));
      mpcRequestPublisher.publishControlRequest(gokartState, mpcPathParameter);
      Thread.sleep(100);// should even work with 30ms
      System.out.println(mpcControlUpdateCapture.cns);
      assertNotNull(mpcControlUpdateCapture.cns);
      mpcRequestPublisher.stop();
      mpcControlUpdateLcmClient.stopSubscriptions();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
