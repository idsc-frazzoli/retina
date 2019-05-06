// code by jph and am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
//import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
//import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
//import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvents;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
//import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
//import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;

/** class is used to develop and test anti lock brake logic */
public class AntilockBrakeModule extends AbstractModule implements LinmotPutProvider {
  private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  // private LinmotGetEvent linmotGetEvent = LinmotGetEvents.ZEROS;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final Timing timing = Timing.started();
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  // private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final HapticSteerConfig hapticSteerConfig;

  public AntilockBrakeModule() {
    this(HapticSteerConfig.GLOBAL);
  }

  public AntilockBrakeModule(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
  }

  @Override // from AbstractModule
  protected void first() {
    LinmotSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    LinmotSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from LinmotPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  double time = timing.seconds();

  public Optional<LinmotPutEvent> putEvent() {
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
        double minSlip = Magnitude.ONE.toDouble(hapticSteerConfig.minSlip);
        double maxSlip = Magnitude.ONE.toDouble(hapticSteerConfig.maxSlip);
        double brakePositionDouble = Magnitude.ONE.toDouble(hapticSteerConfig.fullBraking);
        Scalar brakePosition = RealScalar.of(brakePositionDouble);
        if ((slip1) > minSlip) {
          brakePositionDouble += 0.05;
          LinmotPutEvent relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(brakePosition);
          if (slip1 > maxSlip) {
            brakePositionDouble -= 0.05;
            relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(brakePosition);
          }
          return Optional.of(relativePosition);
        }
        if (slip2 > minSlip) {
          brakePositionDouble += 0.05;
          LinmotPutEvent relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(brakePosition);
          if (slip1 > maxSlip) {
            brakePositionDouble -= 0.1;
            relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(brakePosition);
          }
          return Optional.of(relativePosition);
        }
      }
    }
    return Optional.empty();
  }
}
/** @Override // from LinmotPutProvider
 * public Optional<LinmotPutEvent> putEvent1() {
 * if (lidarLocalizationModule != null) {
 * //full braking is the condition to start the ABS
 * if (Magnitude.ONE.toDouble(linmotGetEvent.getActualPosition()) < Magnitude.ONE.toDouble(HapticSteerConfig.GLOBAL.fullBraking)) { // -500000 is maxBrake
 * // ABS system
 * if ()
 * }
 * double frequency = hapticSteerConfig.absFrequency.number().doubleValue();
 * double amplitude = hapticSteerConfig.absAmplitude.number().doubleValue();
 * double time = timing.seconds();
 * double radian = (2 * Math.PI) * frequency * time;
 * // mean value of 0.5, amplitude of 0.2
 * double sinFunction = (0.5 + amplitude * Math.sin(radian));
 * LinmotPutEvent relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(sinFunction));
 * if (slip1 > hapticSteerConfig.criticalSlip) {
 * // if the slip condition is fulfilled, the ABS acts for 1sec, afterwards,
 * // the condition is checked again
 * double duration = timing.seconds();
 * while (duration < hapticSteerConfig.absDuration) {
 * return Optional.of(relativePosition);
 * }
 * }
 * if (slip2 > hapticSteerConfig.criticalSlip) {
 * double duration = timing.seconds();
 * while (duration < hapticSteerConfig.absDuration) {
 * return Optional.of(relativePosition);
 * }
 * }
 * if (steerColumnTracker.isCalibratedAndHealthy()) {
 * double velocityAngle = Math.atan2(vely, velx);
 * Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
 * Scalar angleGrad = steerMapping.getAngleFromSCE(angleSCE);
 * double angleGradDouble = Magnitude.DEGREE_ANGLE.toDouble(angleGrad);
 * double angleDifference = (Math.abs(angleGradDouble) - Math.abs(velocityAngle));
 * if (angleDifference > Magnitude.ONE.toDouble(hapticSteerConfig.criticalAngle())) {
 * double duration = timing.seconds();
 * while (duration < hapticSteerConfig.absDuration) {
 * return Optional.of(relativePosition);
 * }
 * }
 * }
 * }
 * return Optional.empty();
 * }
 * } */