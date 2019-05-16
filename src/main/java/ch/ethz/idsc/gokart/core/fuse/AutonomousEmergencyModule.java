// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

/** prevents driving if pose is has insufficient quality for timeout duration */
public class AutonomousEmergencyModule extends AbstractModule {
  // TODO
  private static final Scalar BRAKINGTHRESHOLD = Quantity.of(0.5, SI.VELOCITY);
  private static final Scalar BRAKINGVALUE = RealScalar.of(1);
  /** timeout 0.3[s] */
  private final Watchdog localizationWatchdog = SoftWatchdog.barking(0.3);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  private final LinmotGetListener linmotGetListener = new LinmotGetListener() {
    @Override
    public void getEvent(LinmotGetEvent getEvent) {
      isTemperatureOperationSafe = //
          LinmotConfig.GLOBAL.isTemperatureOperationSafe(getEvent.getWindingTemperatureMax());
    }
  };
  private final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent getEvent) {
      fastEnoughToBrake = Scalars.lessThan(BRAKINGTHRESHOLD, //
          ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent).abs());
    }
  };
  final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    @Override // from RimoPutProvider
    public Optional<RimoPutEvent> putEvent() {
      Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
      boostPushed = optional.isPresent() && optional.get().isResetPressed();
      return !safeToDrive()//
          ? Optional.of(RimoPutEvent.PASSIVE)//
          : Optional.empty();
    }

    @Override // from RimoPutProvider
    public ProviderRank getProviderRank() {
      return ProviderRank.EMERGENCY;
    }
  };
  final GokartPoseListener gokartPoseListener = new GokartPoseListener() {
    @Override // from GokartPoseListener
    public void getEvent(GokartPoseEvent gokartPoseEvent) {
      if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent.getQuality()))
        localizationWatchdog.notifyWatchdog();
      // trigger fuse
      boolean instantStop = Scalars.isZero(gokartPoseEvent.getQuality());
      if (boostPushed) {
        isLocalizationBroken = true;
        boostPushed = false;
      }
      isLocalizationBroken = instantStop || isLocalizationBroken || localizationWatchdog.isBarking();
    }
  };
  // when watchdog is triggered, the fuse is set to true.
  private boolean isLocalizationBroken = false;
  private boolean isTemperatureOperationSafe = true;
  private boolean fastEnoughToBrake = true;
  private boolean boostPushed = false;

  // ManualControlLcmClient manualControlLcmClient
  @Override // from AbstractModule
  protected void first() {
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
    manualControlProvider.start();
    RimoSocket.INSTANCE.addPutProvider(rimoPutProvider);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
    LinmotSocket.INSTANCE.addGetListener(linmotGetListener);
    LinmotSocket.INSTANCE.addPutProvider(linmotPutProvider);
    SteerSocket.INSTANCE.addPutProvider(steerPutProvider);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(rimoPutProvider);
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    gokartPoseLcmClient.stopSubscriptions();
    manualControlProvider.stop();
    LinmotSocket.INSTANCE.removeGetListener(linmotGetListener);
    LinmotSocket.INSTANCE.removePutProvider(linmotPutProvider);
    SteerSocket.INSTANCE.removePutProvider(steerPutProvider);
  }

  private boolean safeToDrive() {
    return isTemperatureOperationSafe && !isLocalizationBroken;
  }

  private final SteerPutProvider steerPutProvider = new SteerPutProvider() {
    @Override
    public Optional<SteerPutEvent> putEvent() {
      return !safeToDrive() //
          ? Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0)//
          : Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.EMERGENCY;
    }
  };
  private final LinmotPutProvider linmotPutProvider = new LinmotPutProvider() {
    @Override
    public Optional<LinmotPutEvent> putEvent() {
      return !safeToDrive() && fastEnoughToBrake //
          ? Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(BRAKINGVALUE))//
          : Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.EMERGENCY;
    }
  };
}
