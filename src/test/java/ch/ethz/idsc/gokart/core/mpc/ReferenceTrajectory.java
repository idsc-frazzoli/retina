// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.IOException;
import java.util.Objects;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum ReferenceTrajectory {
  ;
  public static Tensor of(String trackName, int steps, int skip) throws InterruptedException, IOException {
    Tensor trackData = Import.of(HomeDirectory.Documents("TrackID", trackName)).multiply(Quantity.of(1, SI.METER));
    MPCBSplineTrack track = new MPCBSplineTrack(trackData, true);
    return of(track, steps, skip);
  }

  public static Tensor of(MPCPreviewableTrack track, int steps, int skip) throws InterruptedException {
    Tensor positions = Tensors.empty();
    MPCRequestPublisher mpcRequestPublisher = MPCRequestPublisher.dynamic();
    MPCControlUpdateLcmClient mpcControlUpdateLcmClient = new MPCControlUpdateLcmClient();
    MPCControlUpdateCapture mpcControlUpdateCapture = new MPCControlUpdateCapture();
    mpcControlUpdateLcmClient.addListener(mpcControlUpdateCapture);
    mpcControlUpdateLcmClient.startSubscriptions();
    // mpcRequestPublisher.switchToExternalStart(); // TODO MPC manage (external) process somewhere
    GokartState gokartState;
    Tensor pose = track.getStartPose();
    // 44.2575 51.6983
    gokartState = new GokartState(//
        0, //
        1f, //
        0, //
        0, //
        pose.Get(0).number().floatValue(), //
        pose.Get(1).number().floatValue(), //
        pose.Get(2).number().floatValue(), //
        0, //
        0, //
        0, //
        60, //
        0, //
        0);
    MPCOptimizationParameterDynamic optimizationParameterDynamic = new MPCOptimizationParameterDynamic( //
        Quantity.of(10, SI.VELOCITY), //
        Quantity.of(4, SI.ACCELERATION), //
        RealScalar.of(0.02), //
        MPCOptimizationConfig.GLOBAL.specificMoI);
    mpcRequestPublisher.publishOptimizationParameter(optimizationParameterDynamic);
    // lcmMPCControlClient.registerControlUpdateLister(MPCInformationProvider.getInstance());
    Tensor position = gokartState.getCenterPosition();
    MPCPathParameter mpcPathParameter = track.getPathParameterPreview( //
        MPCNative.SPLINE_PREVIEW_SIZE, //
        position, //
        Quantity.of(0.5, SI.METER), //
        RealScalar.of(-0.5), //
        RealScalar.of(0.7));
    mpcRequestPublisher.publishControlRequest(gokartState, mpcPathParameter);
    for (int i = 0; i < steps; i++) {
      Thread.sleep(200);
      System.out.println("send request");
      if (Objects.nonNull(mpcControlUpdateCapture.cns)) {
        gokartState = mpcControlUpdateCapture.cns.steps[skip].gokartState();
        // System.out.println(gokartState.getS());
        position = gokartState.getCenterPosition();
        positions.append(position);
        Scalar changeRate = mpcControlUpdateCapture.cns.steps[0].gokartControl().getudotS();
        Scalar rampupVale = mpcControlUpdateCapture.cns.steps[0].gokartState().getS() //
            .add(changeRate.multiply(Quantity.of(0.1, SI.SECOND)));
        Scalar betaDiff = mpcControlUpdateCapture.cns.steps[1].gokartState().getS().subtract(rampupVale);
        System.out.println("should be zero: " + betaDiff);
        mpcPathParameter = track.getPathParameterPreview(MPCNative.SPLINE_PREVIEW_SIZE, position, Quantity.of(0, SI.METER), //
            RealScalar.of(1), RealScalar.of(0.5));
        System.out.println("progressstart: " + mpcPathParameter.getProgressOnPath());
        mpcRequestPublisher.publishControlRequest(gokartState, mpcPathParameter);
      } else
        System.err.println("lastcns null");
    }
    // mpcRequestPublisher.stop();
    return positions;
  }
}
