// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.bot.se2.pid.PIDTrajectory;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class PIDControllerModule extends PIDControllerBase implements GokartPoseListener {
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent = null;
  private Optional<Tensor> optionalCurve = Optional.empty();
  private int pidIndex;
  private PIDTrajectory previousPID;
  // FIXME MCP systematic error
  private StateTime previousStateTime = new StateTime(Tensors.fromString("{0[m], 0[m], 0}"), Quantity.of(0, SI.SECOND));

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
    if (Objects.nonNull(gokartPoseEvent) && //
        optionalCurve.isPresent()) {
      StateTime stateTime = new StateTime( //
          gokartPoseEvent.getPose(), //
          previousStateTime.time().add(PIDTuningParams.GLOBAL.updatePeriod));
      //
      PIDTrajectory pidTrajectory = new PIDTrajectory( //
          pidIndex, //
          previousPID, //
          PIDTuningParams.GLOBAL.pidGains, //
          optionalCurve.get(), //
          stateTime); //
      //
      Scalar angleOut = pidTrajectory.angleOut(); // TODO comment on unit? -> test
      if (PIDTuningParams.GLOBAL.clip.isInside(angleOut)) {
        this.previousPID = pidTrajectory;
        pidIndex++;
        return Optional.of(angleOut);
      }
      this.previousPID = pidTrajectory;
      pidIndex++;
      return Optional.empty();
    }
    return Optional.empty();
  }

  public void setCurve(Optional<Tensor> curve) {
    // TODO expect that curve has proper units (?)
    // TODO either demand that se2 curve is provided or append angles ...
    // TODO if invalid -> optionalCurve = Optional.empty()
    optionalCurve = curve;
  }

  /* package */ final Optional<Tensor> getCurve() {
    return optionalCurve;
  }
}