// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class VibrateModule extends AbstractModule implements SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  private final Timing timing = Timing.started();

  @Override
  protected void first() {
    SteerSocket.INSTANCE.addPutProvider(this);
    manualControlProvider.start();
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(this);
    manualControlProvider.stop();
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      return steerEvent(manualControlProvider.getManualControl());
    }
    return Optional.empty();
  }

  Optional<SteerPutEvent> steerEvent(Optional<ManualControlInterface> optional) {
    if (optional.isPresent()) {
      ManualControlInterface manualControlInterface = optional.get();
      if (manualControlInterface.isAutonomousPressed()) {
        double frequency = HapticSteerConfig.GLOBAL.vibrationFrequency.number().doubleValue();
        double amplitude = HapticSteerConfig.GLOBAL.vibrationAmplitude.number().doubleValue();
        double time = timing.seconds();
        double radian = (2 * Math.PI) * frequency * time;
        return Optional.of(SteerPutEvent.createOn(Quantity.of((float) Math.sin(radian) * amplitude, "SCT")));
      }
    }
    return Optional.empty();
  }
}
