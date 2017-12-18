// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.glc.PurePursuit;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseLcmClient;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseListener;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;

public class PurePursuitModule extends AbstractClockedModule implements GokartPoseListener {
  public static final Tensor CURVE = DubendorfCurve.OVAL;
  public static final Clip VALID_RANGE = SteerConfig.GLOBAL.getAngleLimit();
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  final PurePursuitSteer purePursuitSteer = new PurePursuitSteer();
  final PurePursuitRimo purePursuitRimo = new PurePursuitRimo();
  // ---
  private GokartPoseEvent gokartPoseEvent = null;

  @Override // from AbstractModule
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    purePursuitRimo.start();
    purePursuitSteer.start();
  }

  @Override // from AbstractModule
  protected void last() {
    purePursuitRimo.stop();
    purePursuitSteer.stop();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    if (Objects.nonNull(gokartPoseEvent)) {
      Tensor pose = gokartPoseEvent.getPose(); // latest pose
      Optional<Scalar> optional = getLookAhead(pose, CURVE);
      boolean status = optional.isPresent();
      if (status) {
        Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(optional.get());
        status = VALID_RANGE.isInside(angle);
        if (status)
          purePursuitSteer.setHeading(angle);
      }
      purePursuitSteer.setOperational(status);
      purePursuitRimo.setOperational(status);
    }
  }

  /* package */ static Optional<Scalar> getLookAhead(Tensor pose, Tensor curve) {
    Tensor poseNoUnits = pose.map(scalar -> RealScalar.of(scalar.number()));
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(poseNoUnits).inverse();
    Tensor tensor = Tensor.of(curve.stream().map(tensorUnaryOperator));
    Scalar distance = PursuitConfig.GLOBAL.lookAheadMeter();
    Optional<Tensor> aheadTrail = CurveUtils.getAheadTrail(tensor, distance);
    if (aheadTrail.isPresent())
      return PurePursuit.turningRate(aheadTrail.get(), distance);
    return Optional.empty();
  }

  @Override // from AbstractClockedModule
  protected double getPeriod() {
    return PursuitConfig.GLOBAL.updatePeriodSeconds().number().doubleValue();
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 50[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
