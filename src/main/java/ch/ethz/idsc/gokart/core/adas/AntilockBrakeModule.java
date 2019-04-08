// code by jph and am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

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
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.RealScalar;
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
      Tensor velocityOrigin = lidarLocalizationModule.getVelocityXY();
      double x = Magnitude.ARMS.toDouble(velocityOrigin.Get(1));
      double y = Magnitude.ARMS.toDouble(velocityOrigin.Get(2));
      // "angular velocity of the go-kart", x-Speed devided by the radius of the rear tire
      Scalar angularRate_Origin = velocityOrigin.Get(1).divide(ChassisGeometry.GLOBAL.tireRadiusRear);
      Scalar one = RealScalar.of(1);
      Tensor oneTensor = Tensors.of(one, one);
      // Slip = 0 if velocity of tire equals velocity of the gokart, Slip = 1 if velocity of tire is zero
      Tensor slip = Tensors.of(//
          oneTensor.add((angularRate_Y_pair.Get(1).divide(angularRate_Origin)).negate()), //
          oneTensor.add((angularRate_Y_pair.Get(2).divide(angularRate_Origin)).negate()));
      // the brake cannot be constantly applied otherwise the brake motor heats up too much
      double slip1 = Magnitude.ARMS.toDouble(slip.Get(1)); // was ist der Unterschied von .todouble zu .number().doubleValue?
      double slip2 = Magnitude.ARMS.toDouble(slip.Get(2));
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
        double velocityAngle = Math.atan2(y, x);
        Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
        Scalar angleGrad = steerMapping.getAngleFromSCE(angleSCE);
        double angleGradDouble = Magnitude.ARMS.toDouble(angleGrad);
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