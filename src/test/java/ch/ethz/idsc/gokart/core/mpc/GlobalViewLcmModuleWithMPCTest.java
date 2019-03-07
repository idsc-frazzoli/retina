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
    LcmMPCControlClient lcmMPCControlClient = new LcmMPCControlClient();
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    lcmMPCControlClient.switchToExternalStart();
    lcmMPCControlClient.start();
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
    MPCOptimizationParameter optimizationParameter = new MPCOptimizationParameter(//
        Quantity.of(20, SI.VELOCITY), //
        Quantity.of(5, SI.ACCELERATION), Quantity.of(10, SI.ACCELERATION));
    /* MPCOptimizationParameter optimizationParameter = new MPCOptimizationParameter(//
     * Quantity.of(20, SI.VELOCITY), //
     * Quantity.of(5, SI.ACCELERATION), Quantity.of(10, SI.ACCELERATION),
     * Quantity.); */
    lcmMPCControlClient.publishOptimizationParameter(optimizationParameter);
    lcmMPCControlClient.registerControlUpdateLister(MPCInformationProvider.getInstance());
    DubendorfTrack track = DubendorfTrack.CHICANE;
    MPCSimpleBraking mpcSimpleBraking = new MPCSimpleBraking();
    MPCOpenLoopSteering mpcOpenLoopSteering = new MPCOpenLoopSteering();
    MPCTorqueVectoringPower mpcTorqueVectoringPower = new MPCTorqueVectoringPower(new FakeNewsEstimator(Timing.started()), mpcOpenLoopSteering);
    lcmMPCControlClient.registerControlUpdateLister(mpcSimpleBraking);
    lcmMPCControlClient.registerControlUpdateLister(mpcOpenLoopSteering);
    lcmMPCControlClient.registerControlUpdateLister(mpcTorqueVectoringPower);
    Tensor position = gokartState.getCenterPosition();
    MPCPathParameter mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINE_PREVIEW_SIZE, position, Quantity.of(0, SI.METER), RealScalar.ZERO);
    lcmMPCControlClient.publishControlRequest(gokartState, mpcPathParameter);
    Thread.sleep(1000);
    for (int i = 0; i < 200; i++) {
      System.out.println("send request");
      if (Objects.nonNull(lcmMPCControlClient.lastcns)) {
        gokartState = lcmMPCControlClient.lastcns.steps[3].gokartState;
        // System.out.println(gokartState.getS());
        position = gokartState.getCenterPosition();
        Scalar changeRate = lcmMPCControlClient.lastcns.steps[0].gokartControl.getudotS();
        Scalar rampupVale = lcmMPCControlClient.lastcns.steps[0].gokartState.getS()//
            .add(changeRate.multiply(Quantity.of(0.1, SI.SECOND)));
        Scalar betaDiff = lcmMPCControlClient.lastcns.steps[1].gokartState.getS().subtract(rampupVale);
        // TODO do this with the correct unit
        // assertTrue(Chop._07.close(betaDiff, "zero");
        // mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINEPREVIEWSIZE, position, Quantity.of(0, SI.METER));
        mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINE_PREVIEW_SIZE, position, Quantity.of(0, SI.METER), RealScalar.of(0.5));
        System.out.println("progressstart: " + mpcPathParameter.getProgressOnPath());
        lcmMPCControlClient.publishControlRequest(gokartState, mpcPathParameter);
        Thread.sleep(100);
        System.out.println("Braking value: " + mpcSimpleBraking.getBraking(lcmMPCControlClient.lastcns.steps[0].gokartState.getTime()));
        System.out.println("steering value: " + mpcOpenLoopSteering.getSteering(lcmMPCControlClient.lastcns.steps[0].gokartState.getTime()));
        System.out.println("power value: " + mpcTorqueVectoringPower.getPower(lcmMPCControlClient.lastcns.steps[0].gokartState.getTime()));
        System.out.println("time value: " + gokartState.getTime());
      } else
        System.err.println("lastcns null");
    }
    globalViewLcmModule.last();
    lcmMPCControlClient.stop();
  }
}
