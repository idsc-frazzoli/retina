// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class VibrateModule extends AbstractModule implements SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  private final Timing timing = Timing.started();

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

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      return SteerEvent(manualControlProvider.getManualControl());
    }
    return Optional.empty();
  }

  public Optional<SteerPutEvent> SteerEvent(Optional<ManualControlInterface> optional) {
    if (optional.isPresent()) {
      ManualControlInterface gokartJoystickInterface = optional.get();
      if (gokartJoystickInterface.isAutonomousPressed()) {
        double frequency = SteerConfig.GLOBAL.VibrationFrequency.number().doubleValue();
        double amplitude = SteerConfig.GLOBAL.VibrationAmplitude.number().doubleValue();
        double time = timing.seconds();
        double radian = (2 * Math.PI) * frequency * time;
        return Optional.of(SteerPutEvent.createOn(Quantity.of((float) Math.sin(radian) * amplitude, "SCT")));
      }
    }
    return Optional.empty();
  }
}
