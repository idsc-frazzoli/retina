// code by mh
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
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
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
public class AutonomousSafetyModule extends AbstractModule {
  private static final ProviderRank PROVIDER_RANK = ProviderRank.SAFETY;
  // TODO move to config file
  private static final Scalar BRAKINGTHRESHOLD = Quantity.of(0.5, SI.VELOCITY);
  private static final Scalar BRAKINGVALUE = RealScalar.of(0.95);
  /** timeout 0.3[s] */
  private final Watchdog localizationWatchdog = SoftWatchdog.barking(0.3);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.getProvider();
  final LinmotGetListener linmotGetListener = new LinmotGetListener() {
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
  final AutonomySafetyRimo autonomySafetyRimo = new AutonomySafetyRimo(this::isSafeToDrive);
  final AutonomySafetySteer autonomySafetySteer = new AutonomySafetySteer(this::isSafeToDrive);
  private final LinmotPutProvider linmotPutProvider = new LinmotPutProvider() {
    @Override
    public Optional<LinmotPutEvent> putEvent() {
      return isSafeToDrive() || !fastEnoughToBrake //
          ? Optional.empty()
          : Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(BRAKINGVALUE));
    }

    @Override
    public ProviderRank getProviderRank() {
      return PROVIDER_RANK;
    }
  };
  final GokartPoseListener gokartPoseListener = new GokartPoseListener() {
    @Override // from GokartPoseListener
    public void getEvent(GokartPoseEvent gokartPoseEvent) {
      if (isLocalizationBroken) {
        Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
        if (optional.isPresent() && optional.get().isResetPressed())
          isLocalizationBroken = false;
      }
      // ---
      /* or-equals implies that manual reset is required */
      isLocalizationBroken |= Scalars.isZero(gokartPoseEvent.getQuality());
      if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent.getQuality()))
        localizationWatchdog.notifyWatchdog();
      // trigger fuse
      isLocalizationBroken |= localizationWatchdog.isBarking();
    }
  };
  // when watchdog is triggered, the fuse is set to true.
  private boolean isLocalizationBroken = true;
  private boolean isTemperatureOperationSafe = false;
  private boolean fastEnoughToBrake = true;

  @Override // from AbstractModule
  protected void first() {
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(autonomySafetyRimo);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
    LinmotSocket.INSTANCE.addGetListener(linmotGetListener);
    LinmotSocket.INSTANCE.addPutProvider(linmotPutProvider);
    SteerSocket.INSTANCE.addPutProvider(autonomySafetySteer);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(autonomySafetyRimo);
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    gokartPoseLcmClient.stopSubscriptions();
    LinmotSocket.INSTANCE.removeGetListener(linmotGetListener);
    LinmotSocket.INSTANCE.removePutProvider(linmotPutProvider);
    SteerSocket.INSTANCE.removePutProvider(autonomySafetySteer);
  }

  private boolean isSafeToDrive() {
    if (SafetyConfig.GLOBAL.checkAutonomy) {
      // if (!isTemperatureOperationSafe)
      // System.err.println("linmot temperature");
      // if (isLocalizationBroken)
      // System.err.println("localization broken - press reset");
      return isTemperatureOperationSafe && !isLocalizationBroken;
    }
    return true;
  }
}
