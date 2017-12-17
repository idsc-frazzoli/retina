// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseLcmClient;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseListener;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ArcTan;

public class PurePursuitModule extends AbstractClockedModule implements GokartPoseListener {
  public static final Tensor CURVE = DubendorfCurve.OVAL;
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
      Optional<Tensor> optional = getLookAhead(pose, CURVE);
      boolean status = optional.isPresent();
      if (status) {
        Tensor lookAhead = optional.get();
        Scalar angle = ArcTan.of(lookAhead.Get(0), lookAhead.Get(1));
        purePursuitSteer.setHeading(angle);
      }
      purePursuitSteer.setOperational(status);
      purePursuitRimo.setOperational(status);
    }
  }

  /* package */ static Optional<Tensor> getLookAhead(Tensor pose, Tensor curve) {
    Tensor poseNoUnits = pose.map(scalar -> RealScalar.of(scalar.number()));
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(poseNoUnits).inverse();
    Tensor beacons = Tensor.of(curve.stream().map(tensorUnaryOperator));
    int index = CurveUtils.closestCloserThan(beacons, PursuitConfig.GLOBAL.maxDistanceMeter());
    if (0 <= index)
      return CurveUtils.interpolate(beacons, index, PursuitConfig.GLOBAL.lookAheadMeter());
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
