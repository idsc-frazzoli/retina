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
      // the brake cannot be constantly applied otherwise the brake motor heats up too much
      double slip1 = Magnitude.ONE.toDouble(slip.Get(0));
      double slip2 = Magnitude.ONE.toDouble(slip.Get(1));
      double minSlip = Magnitude.ONE.toDouble(HapticSteerConfig.GLOBAL.minSlip);
      if (slip1 > minSlip) {
        return vibrate();
      }
      if (slip2 > minSlip) {
        return vibrate();
      }
      double velocityAngle = Math.atan2(Magnitude.VELOCITY.toDouble(velocityOrigin.Get(1)), Magnitude.VELOCITY.toDouble(velocityOrigin.Get(0)));
      // velocityAngle is in radian
      Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
      Scalar angleGrad = steerMapping.getRatioFromSCE(angleSCE); // FIXME units have changed
      double angleGradDouble = Magnitude.DEGREE_ANGLE.toDouble(angleGrad);
      double angleDifference = (Math.abs(angleGradDouble) - Math.abs(velocityAngle));
      if (angleDifference > Magnitude.ONE.toDouble(hapticSteerConfig.criticalAngle())) {
        return vibrate();
      } else
        return SteerPutEvent.createOn(Quantity.of(0, "SCT"));
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