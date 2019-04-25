// code by jph and am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
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
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final Timing timing = Timing.started();
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();

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

  @Override // from LinmotPutProvider
  public Optional<LinmotPutEvent> putEvent() {
    if (lidarLocalizationModule != null) {
      // TODO AM unfinished implementation
      Tensor angularRate_Y_pair = rimoGetEvent.getAngularRate_Y_pair();
      // 1:left, 2: right
      Tensor velocityOrigin = lidarLocalizationModule.getVelocity().extract(0, 2);
      double velx = Magnitude.VELOCITY.toDouble(velocityOrigin.Get(0));
      double vely = Magnitude.VELOCITY.toDouble(velocityOrigin.Get(1));
      // "angular velocity of the go-kart", x-Speed devided by the radius of the rear tire
      Scalar angularRate_Origin = velocityOrigin.Get(0).divide(RimoTireConfiguration._REAR.radius());
      Tensor oneTensor = Tensors.vector(1.0, 1.0);
      // Slip = 0 if velocity of tire equals velocity of the gokart, Slip = 1 if velocity of tire is zero
      Tensor slip = Tensors.of( //
          oneTensor.add((angularRate_Y_pair.Get(0).divide(angularRate_Origin)).negate()), //
          oneTensor.add((angularRate_Y_pair.Get(1).divide(angularRate_Origin)).negate()));
      // the brake cannot be constantly applied otherwise the brake motor heats up too much
      // was ist der Unterschied von .todouble zu .number().doubleValue?
      double slip1 = Magnitude.ONE.toDouble(slip.Get(0));
      double slip2 = Magnitude.ONE.toDouble(slip.Get(1));
      // ABS system
      double frequency = HapticSteerConfig.GLOBAL.absFrequency.number().doubleValue();
      double amplitude = HapticSteerConfig.GLOBAL.absAmplitude.number().doubleValue();
      double time = timing.seconds();
      double radian = (2 * Math.PI) * frequency * time;
      // mean value of 0.5, amplitude of 0.2
      double sinFunction = (0.5 + amplitude * Math.sin(radian));
      LinmotPutEvent relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(sinFunction));
      if (slip1 > HapticSteerConfig.GLOBAL.criticalSlip) {
        // if the slip condition is fulfilled, the ABS acts for 1sec, afterwards,
        // the condition is checked again
        double duration = timing.seconds();
        while (duration < HapticSteerConfig.GLOBAL.absDuration) {
          return Optional.of(relativePosition);
        }
      }
      if (slip2 > HapticSteerConfig.GLOBAL.criticalSlip) {
        double duration = timing.seconds();
        while (duration < HapticSteerConfig.GLOBAL.absDuration) {
          return Optional.of(relativePosition);
        }
      }
      if (steerColumnTracker.isCalibratedAndHealthy()) {
        double velocityAngle = Math.atan2(vely, velx);
        Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
        Scalar angleGrad = steerMapping.getAngleFromSCE(angleSCE);
        double angleGradDouble = Magnitude.DEGREE_ANGLE.toDouble(angleGrad);
        double angleDifference = (Math.abs(angleGradDouble) - Math.abs(velocityAngle));
        if (angleDifference > HapticSteerConfig.GLOBAL.criticalAngle) {
          double duration = timing.seconds();
          while (duration < HapticSteerConfig.GLOBAL.absDuration) {
            return Optional.of(relativePosition);
          }
        }
      }
    }
    return Optional.empty();
  }
}