// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcTan;

public class PIDController extends PIDControllerModule implements GokartPoseListener {
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  GokartPoseEvent gokartPoseEvent = null;
  private Optional<Tensor> optionalCurve = Optional.empty();
  // private Scalar previousAngleError = RealScalar.ZERO;

  public PIDController(PIDTuningParams tuningParams) {
    super(tuningParams);
  }

  @Override // from GoKartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    this.gokartPoseEvent = getEvent;
  }

  public void setCurve(Optional<Tensor> curve) {
    optionalCurve = curve;
  }

  /* package */ final Optional<Tensor> getCurve() {
    return optionalCurve;
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

  public static int closest(Tensor curve, Tensor point) {
    return ArgMin.of(Tensor.of(curve.stream().map(row -> Norm._2.between(row, point))));
  }

  public static Tensor trajAngle(Tensor curve, Tensor point) {
    int index = closest(curve, point);
    int nextIndex = index + 1;
    if (nextIndex > curve.length()) // TODO MCP Write this clearlier
      nextIndex = 0;
    return ArcTan.of(curve.Get(nextIndex).subtract(curve.Get(index)));
  }

  @Override
  protected Optional<Scalar> deriveHeading() {
    GokartPoseEvent gokartPoseEvent = this.gokartPoseEvent;
    if (Objects.nonNull(gokartPoseEvent) && optionalCurve.isPresent()) {
      Scalar angle = (Scalar) gokartPoseEvent.getPose().get(2);
      Tensor position = gokartPoseEvent.getPose().extract(0, 2);
      // find closest points on trajectory and angle
      Tensor closest = optionalCurve.get().get(closest(optionalCurve.get(), gokartPoseEvent.getPose().extract(0, 2)));
      Tensor trajAngle = trajAngle(optionalCurve.get(), gokartPoseEvent.getPose().extract(0, 2));
      // measure error
      Scalar errorPose = Norm._2.of(position.subtract(closest));
      Scalar errorAngle = angle.subtract(trajAngle);
      Scalar iErrorAngle = errorAngle.multiply(PIDTuningParams.GLOBAL.updatePeriod);
      // Scalar dErrorAngle = errorAngle.subtract(previousAngleError);
      // set previous error
      // setPreviousError(errorAngle);
      // angle output
      Scalar pTermPose = errorPose.multiply(PIDTuningParams.GLOBAL.pGainPose);
      Scalar pTermAngle = errorAngle.multiply(PIDTuningParams.GLOBAL.pGain);
      Scalar iTermAngle = iErrorAngle.multiply(PIDTuningParams.GLOBAL.iGain);
      Scalar angleOut = pTermPose.add(pTermAngle).add(iTermAngle);
      return Optional.of(angleOut);
    }
    return Optional.empty();
  }
  // private void setPreviousError(Tensor errorAngle) {
  // previousAngleError = (Scalar) errorAngle;
  // }
}
