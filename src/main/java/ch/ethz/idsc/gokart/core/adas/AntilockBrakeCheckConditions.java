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
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** class is used to develop and test anti lock brake logic */
public class AntilockBrakeCheckConditions extends AbstractModule implements SteerPutProvider {
  // private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final HapticSteerConfig hapticSteerConfig;
  private SteerVibrationModule steerVibration = new SteerVibrationModule();

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

  @Override // from LinmotPutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      if (lidarLocalizationModule != null) {
        Tensor angularRate_Y_pair = rimoGetEvent.getAngularRate_Y_pair();
        // 0:left, 1: right
        Tensor velocityOrigin = lidarLocalizationModule.getVelocity();
        Scalar angularRate_Origin = velocityOrigin.Get(0).divide(RimoTireConfiguration._REAR.radius());
        Tensor angularRage_Origin_pair = Tensors.of(angularRate_Origin, angularRate_Origin);
        Tensor slip = angularRate_Y_pair.subtract(angularRage_Origin_pair);
        // the brake cannot be constantly applied otherwise the brake motor heats up too much
        double slip1 = Magnitude.ONE.toDouble(slip.Get(0));
        double slip2 = Magnitude.ONE.toDouble(slip.Get(1));
        double minSlip = Magnitude.ONE.toDouble(HapticSteerConfig.GLOBAL.minSlip);
        if (slip1 > minSlip) {
          steerVibration.putEvent();
        }
        if (slip2 > minSlip) {
          steerVibration.putEvent();
        }
        Scalar velocityAngle = ArcTan2D.of(velocityOrigin);
        // TODO AM can use ArcTan2D.of(velocityOrigin);
        // velocityAngle is in radian
        Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
        Scalar angleGrad = steerMapping.getRatioFromSCE(angleSCE);
        double angleGradDouble = Magnitude.DEGREE_ANGLE.toDouble(angleGrad);
        double velocityAngleDouble = Magnitude.DEGREE_ANGLE.toDouble(velocityAngle);
        double angleDifference = (Math.abs(angleGradDouble) - Math.abs(velocityAngleDouble));
        if (angleDifference > Magnitude.ONE.toDouble(hapticSteerConfig.criticalAngle())) {
          steerVibration.putEvent();
        }
      }
      return Optional.empty();
    }
    return Optional.empty();
  }
}