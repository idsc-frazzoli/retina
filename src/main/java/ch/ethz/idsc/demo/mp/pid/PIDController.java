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
    return ArcTan.of(curve.Get(index + 1).subtract(curve.Get(index)));
  }

  @Override
  protected Optional<Scalar> deriveHeading() {
    GokartPoseEvent gokartPoseEvent = this.gokartPoseEvent;
    if (Objects.nonNull(gokartPoseEvent) && optionalCurve.isPresent()) {
      Tensor pose = gokartPoseEvent.getPose();
      Tensor angle = pose.get(2);
      Tensor position = gokartPoseEvent.getPose().extract(0, 2);
      Tensor curve = optionalCurve.get();
      // find closest points on trajectory and angle
      Tensor closest = curve.get(closest(curve, position));
      Tensor trajAngle = trajAngle(curve, position);
      // measure error
      Tensor pErrorAngle = angle.subtract(trajAngle);
      Tensor pErrorPose = position.subtract(closest);
      Scalar iErrorAngle;
      Scalar iErrorPose;
      Scalar dErrorPose;
      Scalar dErrorAngle;
      // angle output
      Tensor pTermPose = pErrorPose.multiply(PIDTuningParams.GLOBAL.pGainPose);
      Tensor pTermAngle = pErrorAngle.multiply(PIDTuningParams.GLOBAL.pGain);
      Scalar angleOut = (Scalar) pErrorPose.add(pTermAngle);
      return Optional.of(angleOut);
    }
    return Optional.empty();
  }
}
