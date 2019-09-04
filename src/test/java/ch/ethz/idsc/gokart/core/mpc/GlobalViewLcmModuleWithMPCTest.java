// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GlobalViewLcmModuleWithMPCTest extends TestCase {
  GokartState gokartState;

  public void testSimple() throws Exception {
    MPCRequestPublisher mpcRequestPublisher = MPCRequestPublisher.kinematic();
    MPCControlUpdateLcmClient mpcControlUpdateLcmClient = new MPCControlUpdateLcmClient();
    MPCControlUpdateCapture mpcControlUpdateCapture = new MPCControlUpdateCapture();
    mpcControlUpdateLcmClient.addListener(mpcControlUpdateCapture);
    mpcControlUpdateLcmClient.startSubscriptions();
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    // mpcRequestPublisher.switchToExternalStart(); // TODO MPC manage (external) process somewhere
    globalViewLcmModule.first();
    // 44.2575 51.6983
    gokartState = new GokartState(//
        11, //
        1f, //
        0, //
        0, //
        44.3f, //
        51.8f, //
        0.6f, //
        0, //
        0, //
        0, 60);
    // MPCOptimizationParameter optimizationParameter = new MPCOptimizationParameter(Quantity.of(20, SI.VELOCITY));
    MPCOptimizationParameterKinematic optimizationParameter = TestHelper.optimizationParameterKinematic( //
        Quantity.of(20, SI.VELOCITY), //
        Quantity.of(5, SI.ACCELERATION), //
        Quantity.of(10, SI.ACCELERATION));
    /* MPCOptimizationParameter optimizationParameter = new MPCOptimizationParameter(//
     * Quantity.of(20, SI.VELOCITY), //
     * Quantity.of(5, SI.ACCELERATION), Quantity.of(10, SI.ACCELERATION),
     * Quantity.); */
    mpcRequestPublisher.publishOptimizationParameter(optimizationParameter);
    MPCBSplineTrack track = DubendorfTrack.CHICANE;
    MPCSimpleBraking mpcSimpleBraking = new MPCSimpleBraking();
    MPCOpenLoopSteering mpcOpenLoopSteering = new MPCOpenLoopSteering();
    MPCTorqueVectoringPower mpcTorqueVectoringPower = new MPCTorqueVectoringPower(new FakeNewsEstimator(Timing.started()), mpcOpenLoopSteering);
    mpcControlUpdateLcmClient.addListener(mpcSimpleBraking);
    mpcControlUpdateLcmClient.addListener(mpcOpenLoopSteering);
    mpcControlUpdateLcmClient.addListener(mpcTorqueVectoringPower);
    Tensor position = gokartState.getCenterPosition();
    MPCPathParameter mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINE_PREVIEW_SIZE, position, Quantity.of(0, SI.METER), RealScalar.ZERO,
        RealScalar.ZERO);
    mpcRequestPublisher.publishControlRequest(gokartState, mpcPathParameter);
    Thread.sleep(1000);
    for (int i = 0; i < 200; i++) {
      System.out.println("send request");
      if (Objects.nonNull(mpcControlUpdateCapture.cns)) {
        gokartState = mpcControlUpdateCapture.cns.steps[3].gokartState();
        // System.out.println(gokartState.getS());
        position = gokartState.getCenterPosition();
        Scalar changeRate = mpcControlUpdateCapture.cns.steps[0].gokartControl().getudotS();
        Scalar rampupVale = mpcControlUpdateCapture.cns.steps[0].gokartState().getS()//
            .add(changeRate.multiply(Quantity.of(0.1, SI.SECOND)));
        Scalar betaDiff = mpcControlUpdateCapture.cns.steps[1].gokartState().getS().subtract(rampupVale);
        // TODO MH do this with the correct unit
        System.out.println("betaDiff=" + betaDiff);
        // assertTrue(Chop._07.close(betaDiff, "zero");
        // mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINEPREVIEWSIZE, position, Quantity.of(0, SI.METER));
        mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINE_PREVIEW_SIZE, position, Quantity.of(0, SI.METER), RealScalar.of(0.1),
            RealScalar.of(0.8));
        System.out.println("progressstart: " + mpcPathParameter.getProgressOnPath());
        mpcRequestPublisher.publishControlRequest(gokartState, mpcPathParameter);
        Thread.sleep(1000);
        System.out.println("Braking value: " + mpcSimpleBraking.getBraking(mpcControlUpdateCapture.cns.steps[0].gokartState().getTime()));
        System.out.println("steering value: " + mpcOpenLoopSteering.getSteering(mpcControlUpdateCapture.cns.steps[0].gokartState().getTime()));
        System.out.println("power value: " + mpcTorqueVectoringPower.getPower(mpcControlUpdateCapture.cns.steps[0].gokartState().getTime()));
        System.out.println("time value: " + gokartState.getTime());
      } else
        System.err.println("lastcns null");
    }
    globalViewLcmModule.last();
    // mpcRequestPublisher.stop();
    mpcControlUpdateLcmClient.stopSubscriptions();
  }
}
