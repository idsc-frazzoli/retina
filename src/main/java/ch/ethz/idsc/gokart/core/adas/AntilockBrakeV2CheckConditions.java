// code by am
// goal is to check via vibration if the given slipping conditions are fulfilled
// Hilfsmodul für das Erstellen des ABS
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

/** class is used to develop and test anti lock brake logic */
public class AntilockBrakeV2CheckConditions extends AbstractModule implements SteerPutProvider {
  // private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final Timing timing = Timing.started();
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final HapticSteerConfig hapticSteerConfig;

  public AntilockBrakeV2CheckConditions() {
    this(HapticSteerConfig.GLOBAL);
  }

  public AntilockBrakeV2CheckConditions(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
  }

  @Override
  protected void first() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  public SteerPutEvent vibrate() {
    double frequency = HapticSteerConfig.GLOBAL.vibrationFrequency.number().doubleValue();
    double amplitude = HapticSteerConfig.GLOBAL.vibrationAmplitude.number().doubleValue();
    double time = timing.seconds();
    double radian = (2 * Math.PI) * frequency * time;
    return SteerPutEvent.createOn(Quantity.of((float) Math.sin(radian) * amplitude, "SCT"));
  }

  public SteerPutEvent putEvent1(Tensor angularRate_Y_pair, Tensor velocityOrigin) {
    if (lidarLocalizationModule != null) {
      Scalar angularRate_Origin = velocityOrigin.Get(0).divide(RimoTireConfiguration._REAR.radius());
      Tensor angularRage_Origin_pair = Tensors.of(angularRate_Origin, angularRate_Origin);
      Tensor slip = angularRate_Y_pair.subtract(angularRage_Origin_pair);
      System.out.println(slip);
      // the brake cannot be constantly applied otherwise the brake motor heats up too much
      double slip1 = Magnitude.ONE.toDouble(slip.Get(0));
      double slip2 = Magnitude.ONE.toDouble(slip.Get(1));
      double minSlip = Magnitude.ONE.toDouble(hapticSteerConfig.minSlip);
      if (slip1 > minSlip)
        return vibrate();
      if (slip2 > minSlip)
        return vibrate();
      Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
      Scalar ratio = steerMapping.getRatioFromSCE(angleSCE);
      AngularSlip angularSlip = new AngularSlip(velocityOrigin.Get(0), ratio, velocityOrigin.Get(2));
      // TODO AM AngularSlip::toString
      System.out.println(angularSlip);
      return vibrate();
    }
    return SteerPutEvent.createOn(Quantity.of(0, "SCT"));
  }

  @Override // from LinmotPutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      Optional.of(putEvent1(rimoGetEvent.getAngularRate_Y_pair(), lidarLocalizationModule.getVelocity()));
    }
    return Optional.empty();
  }
}