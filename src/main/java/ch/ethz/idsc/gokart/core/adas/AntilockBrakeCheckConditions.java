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
public class AntilockBrakeCheckConditions extends AbstractModule implements SteerPutProvider {
  // private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final Timing timing = Timing.started();
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final HapticSteerConfig hapticSteerConfig;

  public AntilockBrakeCheckConditions() {
    this(HapticSteerConfig.GLOBAL);
  }

  public AntilockBrakeCheckConditions(HapticSteerConfig hapticSteerConfig) {
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
  
  public Optional<SteerPutEvent> vibrate() {
    double frequency = HapticSteerConfig.GLOBAL.vibrationFrequency.number().doubleValue();
    double amplitude = HapticSteerConfig.GLOBAL.vibrationAmplitude.number().doubleValue();
    double time = timing.seconds();
    double radian = (2 * Math.PI) * frequency * time;
    return Optional.of(SteerPutEvent.createOn(Quantity.of((float) Math.sin(radian) * amplitude, "SCT")));
  }

  @Override // from LinmotPutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      if (lidarLocalizationModule != null) {
        Tensor angularRate_Y_pair = rimoGetEvent.getAngularRate_Y_pair();
        // 0:left, 1: right
        Tensor VelocityOrigin = lidarLocalizationModule.getVelocity();
        Scalar angularRate_Origin = VelocityOrigin.Get(0).divide(RimoTireConfiguration._REAR.radius());
        Tensor angularRage_Origin_pair = Tensors.of(angularRate_Origin, angularRate_Origin);
        Tensor slip = angularRate_Y_pair.subtract(angularRage_Origin_pair);
        // the brake cannot be constantly applied otherwise the brake motor heats up too much
        double slip1 = Magnitude.ONE.toDouble(slip.Get(0));
        double slip2 = Magnitude.ONE.toDouble(slip.Get(1));
        if (slip1 > hapticSteerConfig.criticalSlip) {
          vibrate();
        }
        if (slip2 > hapticSteerConfig.criticalSlip) {
          vibrate();
        }
        double velocityAngle = Math.atan2(Magnitude.VELOCITY.toDouble(VelocityOrigin.Get(1)), Magnitude.VELOCITY.toDouble(VelocityOrigin.Get(0)));
        // velocityAngle is in radian
        Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
        Scalar angleGrad = steerMapping.getAngleFromSCE(angleSCE);
        double angleGradDouble = Magnitude.DEGREE_ANGLE.toDouble(angleGrad);
        double angleDifference = (Math.abs(angleGradDouble) - Math.abs(velocityAngle));
        if (angleDifference > Magnitude.ONE.toDouble(hapticSteerConfig.criticalAngle())) {
          vibrate();
        }
      }
      return Optional.empty();
    }
    return Optional.empty();
  }
}