// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.bot.se2.glc.PIDCurveHelper;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class PIDControllerModule extends PIDControllerBase implements GokartPoseListener {
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent = null;
  private Optional<Tensor> optionalCurve = Optional.empty();
  // private Scalar previousAngleError = RealScalar.ZERO;

  public PIDControllerModule(PIDTuningParams pidTuningParams) {
    super(pidTuningParams);
  }

  @Override // from GoKartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    this.gokartPoseEvent = getEvent;
  }

  @Override // from PIDControllerModule
  protected void protected_first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override // from PIDControllerModule
  protected void protected_last() {
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override
  protected Optional<Scalar> deriveHeading() {
    GokartPoseEvent gokartPoseEvent = this.gokartPoseEvent;
    if (Objects.nonNull(gokartPoseEvent) && //
        optionalCurve.isPresent() && //
        PIDCurveHelper.bigEnough(optionalCurve.get())) {
      Scalar angle = gokartPoseEvent.getPose().Get(2);
      Tensor curve = optionalCurve.get();
      Tensor poseXY = GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()).extract(0, 2);
      // find closest points on trajectory and angle
      Tensor closest = curve.get(PIDCurveHelper.closest(curve, poseXY));
      Scalar trajAngle = PIDCurveHelper.trajAngle(curve, poseXY); // TODO MCP Improve scalar assignment
      // measure error
      Scalar errorPose = Quantity.of(Norm._2.between(poseXY, closest), SI.METER);
      Scalar errorAngle = angle.subtract(trajAngle);
      Scalar iErrorAngle = errorAngle.multiply(PIDTuningParams.GLOBAL.updatePeriod);
      // Scalar dErrorAngle = errorAngle.subtract(previousAngleError);
      // set previous error
      // setPreviousError(errorAngle);
      // angle output
      Scalar pTermPose = errorPose.multiply(PIDTuningParams.GLOBAL.pGainPose);
      Scalar pTermAngle = errorAngle.multiply(PIDTuningParams.GLOBAL.pGain);
      Scalar iTermAngle = iErrorAngle.multiply(PIDTuningParams.GLOBAL.iGain);
      iTermAngle = RealScalar.ONE;
      Scalar angleOut = pTermPose.add(pTermAngle).add(iTermAngle);
      return Optional.of(angleOut);
    }
    return Optional.empty();
  }
  // private void setPreviousError(Tensor errorAngle) {
  // previousAngleError = (Scalar) errorAngle;
  // }

  public void setCurve(Optional<Tensor> curve) {
    optionalCurve = curve;
  }

  /* package */ final Optional<Tensor> getCurve() {
    return optionalCurve;
  }
}