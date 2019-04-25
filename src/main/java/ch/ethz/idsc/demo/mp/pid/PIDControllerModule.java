// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.bot.se2.pid.PIDTrajectory;
import ch.ethz.idsc.owl.bot.se2.pid.RnCurveHelper;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class PIDControllerModule extends PIDControllerBase implements GokartPoseListener {
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent = null;
  private Optional<Tensor> optionalCurve = Optional.empty();
  private int pidIntex;
  private PIDTrajectory previousPID;
  private StateTime previousStateTime = new StateTime(Tensors.vector(0,0,0), Quantity.of(0, "s"));

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
      StateTime stateTime = new StateTime(
          GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()),//
          previousStateTime.time().add(PIDTuningParams.GLOBAL.updatePeriod)); //TODO Check this.
      //
      PIDTrajectory pidTrajectory = new PIDTrajectory( //
          pidIntex, //
          previousPID, //
          PIDTuningParams.GLOBAL.pidGains, //
          optionalCurve.get(), //
          stateTime); //
      //
      if (PIDTuningParams.GLOBAL.clip.isInside(pidTrajectory.angleOut())) {
        return Optional.of(pidTrajectory.angleOut());
      }
      this.previousPID = pidTrajectory;
      pidIntex++;
      return Optional.empty();
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