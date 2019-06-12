// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** class is used to develop and test anti lock brake logic */
public class SpeedLimitPerSectionModule extends AbstractModule implements PutProvider<RimoPutEvent>, GokartPoseListener {
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerUno();
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();

  @Override // from AbstractModule
  protected void first() {
    RimoSocket.INSTANCE.addPutProvider(this);
    gokartPoseLcmClient.addListener(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from LinmotPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.SAFETY; // TODO Jan fragen, was das richtige ist.
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent)) { // check availability of pose
      Tensor currAngularRate = rimoGetEvent.getAngularRate_Y_pair();
      Tensor pose = gokartPoseEvent.getPose();
      // straight line function, which divides driving area in two parts: f(x) = -1.182x + 84.647
      Tensor maxVel = Tensors.of(Quantity.of(3, SI.PER_SECOND), Quantity.of(5, SI.PER_SECOND));
      Scalar MaxAngularRate;
      if (Scalars.lessThan(pose.Get(1).add(pose.Get(0).multiply(RealScalar.of(1.182))), RealScalar.of(84.647))) {
        MaxAngularRate = maxVel.Get(0).divide(RimoTireConfiguration._REAR.radius());
      } else
        MaxAngularRate = maxVel.Get(1).divide(RimoTireConfiguration._REAR.radius());
      // Angular velocity is limited by MaxAngularRate
      if (Scalars.lessThan(MaxAngularRate, currAngularRate.Get(0)) || Scalars.lessThan(MaxAngularRate, currAngularRate.Get(1))) {
        return rimoRateControllerWrap.iterate(MaxAngularRate);
      }
    }
    return Optional.empty();
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
