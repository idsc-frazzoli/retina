// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.bot.se2.pid.RnCurveHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

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
        RnCurveHelper.bigEnough(optionalCurve.get())) {
      Tensor curve = optionalCurve.get();
      Tensor poseXYphi = GokartPoseHelper.toUnitless(gokartPoseEvent.getPose());
      //
      //
      //
      Scalar angleOut = RealScalar.ZERO; // TODO CHANGE
      return Optional.of(angleOut);
    }
    return Optional.empty();
  }

  public void setCurve(Optional<Tensor> curve) {
    optionalCurve = curve;
  }

  /* package */ final Optional<Tensor> getCurve() {
    return optionalCurve;
  }
}