// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.bot.se2.pid.Se2CurveHelper;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** class is used to develop and test anti lock brake logic */
/* package */ class LaneKeepingCenterlineModule extends AbstractModule implements GokartPoseListener, RimoPutProvider {
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final MeasurementSlowDownModule slowDown = new MeasurementSlowDownModule();
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  private Optional<Tensor> optionalCurve = Optional.empty();
  private Scalar maxDistance = Quantity.of(1, SI.METER);

  @Override // from AbstractModule
  protected void first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    gokartPoseLcmClient.stopSubscriptions();
  }

  public void setCurve(Optional<Tensor> curve) {
    if (curve.isPresent()) {
      optionalCurve = curve;
    } else {
      System.err.println("Curve missing");
      optionalCurve = Optional.empty();
    }
  }

  final Optional<Tensor> getCurve() {
    return optionalCurve;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    if (optionalCurve.isPresent() && Objects.nonNull(gokartPoseEvent)) {
      Tensor Pose = gokartPoseEvent.getPose();
      Tensor xyPose = Tensors.of(Pose.Get(0), Pose.Get(1));
      Tensor curve = optionalCurve.get();
      Scalar currDistance = Quantity.of(Se2CurveHelper.closest(curve, xyPose), SI.METER);
      if (Scalars.lessThan(maxDistance, currDistance)) {
        return slowDown.putEvent();
      }
    }
    return Optional.empty();
  }
}
