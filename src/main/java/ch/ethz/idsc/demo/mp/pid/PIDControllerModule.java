// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.bot.se2.pid.PIDTrajectory;
import ch.ethz.idsc.owl.bot.se2.pid.Se2CurveUnitCheck;
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
  // ---
  private int pidIndex;
  private PIDTrajectory previousPID;
  private PIDTrajectory currentPID;
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
  protected Optional<Scalar> deriveRatio() {
    if (Objects.nonNull(gokartPoseEvent) && //
        optionalCurve.isPresent()) {
      StateTime stateTime = new StateTime( //
          gokartPoseEvent.getPose(), //
          previousStateTime.time().add(PIDTuningParams.GLOBAL.updatePeriod));
      //
      currentPID = new PIDTrajectory( //
          pidIndex, //
          previousPID, //
          PIDTuningParams.GLOBAL.pidGains, //
          optionalCurve.get(), //
          stateTime); //
      //
      Scalar ratioOut = currentPID.ratioOut(); 
      // TODO assert units are correct (comment on unit? -> test)
      this.previousPID = currentPID;
      pidIndex++;
      return Optional.of(ratioOut);
    }
    this.previousPID = currentPID;
    pidIndex++;
    return Optional.empty();
  }

  public void setCurve(Optional<Tensor> curve) {
    // TODO if invalid -> optionalCurve = Optional.empty() return false;
    // TODO either demand that se2 curve is provided or append angles ...
    if (Se2CurveUnitCheck.that(curve.get(), SI.METER)) {
      optionalCurve = curve;
    }
    optionalCurve = Optional.empty();
  }

  /* package */ final Optional<Tensor> getCurve() {
    return optionalCurve;
  }

  public PIDTrajectory getPID() {
    if (!Objects.isNull(currentPID))
      return currentPID;
    return currentPID;
  }
}