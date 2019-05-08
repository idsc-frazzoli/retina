// code by jph and am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
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
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** class is used to develop and test anti lock brake logic */
public class AntilockBrakeModule extends AbstractModule implements LinmotPutProvider {
  private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final HapticSteerConfig hapticSteerConfig;
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();

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

  // button is pressed -> full brake
  double brakePositionDouble = Magnitude.ONE.toDouble(HapticSteerConfig.GLOBAL.fullBraking);
  Scalar brakePosition = RealScalar.of(brakePositionDouble);

  Optional<LinmotPutEvent> putEvent1() {
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    if (optional.isPresent()) {
      ManualControlInterface manualControlInterface = optional.get();
      if (manualControlInterface.isAutonomousPressed()) {
        return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(brakePosition));
      }
      return Optional.empty();
    }
    return Optional.empty();
  }

  @Override // from LinmotPutProvider
  public Optional<LinmotPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      Optional.of(putEvent1(rimoGetEvent.getAngularRate_Y_pair(), lidarLocalizationModule.getVelocity()));
    }
    return Optional.empty();
  }

  public Optional<LinmotPutEvent> putEvent1(Tensor angularRate_Y_pair, Tensor velocityOrigin) {
    if (lidarLocalizationModule != null) {
      Scalar angularRate_Origin = velocityOrigin.Get(0).divide(RimoTireConfiguration._REAR.radius());
      Tensor angularRate_Origin_pair = Tensors.of(angularRate_Origin, angularRate_Origin);
      Tensor slip = angularRate_Y_pair.subtract(angularRate_Origin_pair);
      // the brake cannot be constantly applied otherwise the brake motor heats up too much
      double slip1 = Magnitude.ONE.toDouble(slip.Get(0));
      double slip2 = Magnitude.ONE.toDouble(slip.Get(1));
      double minSlip = Magnitude.ONE.toDouble(hapticSteerConfig.minSlip);
      double maxSlip = Magnitude.ONE.toDouble(hapticSteerConfig.maxSlip);
      // there is a desired range for slip (in theory 0.1-0.25)
      // if the slip is outside this range, the position of the brake is increased/decreased
      if ((slip1 < minSlip) || (slip1 > maxSlip)) {
        if (slip1 < minSlip) {
          brakePositionDouble += 0.05;
        }
        if (slip1 > maxSlip) {
          brakePositionDouble -= 0.05;
        }
        LinmotPutEvent relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(brakePosition);
        return Optional.of(relativePosition);
      }
      if ((slip2 < minSlip) || (slip2 > maxSlip)) {
        if (slip2 < minSlip) {
          brakePositionDouble += 0.05;
        }
        if (slip2 > maxSlip) {
          brakePositionDouble -= 0.05;
        }
        LinmotPutEvent relativePosition = LinmotPutOperation.INSTANCE.toRelativePosition(brakePosition);
        return Optional.of(relativePosition);
      }    }
    return Optional.empty();
  }
}
