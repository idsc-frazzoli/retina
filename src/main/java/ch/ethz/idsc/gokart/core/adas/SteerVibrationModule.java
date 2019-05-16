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
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sin;

public class SteerVibrationModule extends AbstractModule implements SteerPutProvider {
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final Timing timing = Timing.started();

  @Override
  protected void first() {
    manualControlProvider.start();
    SteerSocket.INSTANCE.addPutProvider(this);
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
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    if (steerColumnTracker.isCalibratedAndHealthy() && optional.isPresent()) {
      ManualControlInterface manualControlInterface = optional.get();
      if (manualControlInterface.isAutonomousPressed())
        return Optional.of(SteerPutEvent.createOn(time2torque(Quantity.of(timing.seconds(), SI.SECOND))));
    }
    return Optional.empty();
  }

  /* package */ Scalar time2torque(Scalar time) {
    Scalar frequency = HapticSteerConfig.GLOBAL.vibrationFrequency;
    Scalar amplitude = HapticSteerConfig.GLOBAL.vibrationAmplitude;
    Scalar radian = frequency.multiply(time).multiply(Pi.TWO);
    return Sin.FUNCTION.apply(radian).multiply(amplitude);
  }
}
