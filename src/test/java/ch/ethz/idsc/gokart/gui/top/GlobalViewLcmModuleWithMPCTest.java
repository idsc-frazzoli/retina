// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.mpc.DubendorfTrack;
import ch.ethz.idsc.gokart.core.mpc.GokartState;
import ch.ethz.idsc.gokart.core.mpc.LcmMPCControlClient;
import ch.ethz.idsc.gokart.core.mpc.MPCInformationProvider;
import ch.ethz.idsc.gokart.core.mpc.MPCNative;
import ch.ethz.idsc.gokart.core.mpc.MPCOpenLoopSteering;
import ch.ethz.idsc.gokart.core.mpc.MPCOptimizationParameter;
import ch.ethz.idsc.gokart.core.mpc.MPCPathParameter;
import ch.ethz.idsc.gokart.core.mpc.MPCSimpleBraking;
import ch.ethz.idsc.gokart.core.mpc.MPCTorqueVectoringPower;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
    gokartState = new GokartState(//
        11, //
        1f, //
        0, //
        0, //
        44.1f, //
        55.6f, //
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
    MPCTorqueVectoringPower mpcTorqueVectoringPower = new MPCTorqueVectoringPower(mpcOpenLoopSteering);
    lcmMPCControlClient.registerControlUpdateLister(mpcSimpleBraking);
    lcmMPCControlClient.registerControlUpdateLister(mpcOpenLoopSteering);
    lcmMPCControlClient.registerControlUpdateLister(mpcTorqueVectoringPower);
    Tensor position = Tensors.of(gokartState.getX(), gokartState.getY());
    MPCPathParameter mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINEPREVIEWSIZE, position, Quantity.of(0, SI.METER), RealScalar.ZERO);
    lcmMPCControlClient.publishControlRequest(gokartState, mpcPathParameter);
    Thread.sleep(1000);
    for (int i = 0; i < 200; i++) {
      System.out.println("send request");
      if (Objects.nonNull(lcmMPCControlClient.lastcns)) {
        gokartState = lcmMPCControlClient.lastcns.steps[3].state;
        // System.out.println(gokartState.getS());
        position = Tensors.of(gokartState.getX(), gokartState.getY());
        Scalar changeRate = lcmMPCControlClient.lastcns.steps[0].control.getudotS();
        Scalar rampupVale = lcmMPCControlClient.lastcns.steps[0].state.getS()//
            .add(changeRate.multiply(Quantity.of(0.1, SI.SECOND)));
        Scalar betaDiff = lcmMPCControlClient.lastcns.steps[1].state.getS().subtract(rampupVale);
        // TODO do this with the correct unit
        // assertTrue(Chop._07.close(betaDiff, "zero");
        mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINEPREVIEWSIZE, position, Quantity.of(0, SI.METER));
        lcmMPCControlClient.publishControlRequest(gokartState, mpcPathParameter);
        Thread.sleep(1000);
        System.out.println("Braking value: " + mpcSimpleBraking.getBraking(lcmMPCControlClient.lastcns.steps[0].state.getTime()));
        System.out.println("steering value: " + mpcOpenLoopSteering.getSteering(lcmMPCControlClient.lastcns.steps[0].state.getTime()));
        System.out.println("power value: " + mpcTorqueVectoringPower.getPower(lcmMPCControlClient.lastcns.steps[0].state.getTime()));
        System.out.println("time value: " + gokartState.getTime());
      } else
        System.err.println("lastcns null");
    }
    globalViewLcmModule.last();
    lcmMPCControlClient.stop();
  }
}
