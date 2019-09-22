// code by em
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.adas.HapticSteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.PRBS7SignedSignal;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class SteerSignalModule extends AbstractModule implements SteerPutProvider {
  private final ScalarUnaryOperator signal;
  private final Timing timing = Timing.stopped();
  private final Clip safety;

  public SteerSignalModule() {
    signal = PRBS7SignedSignal.of(RealScalar.of(0.2));
    safety = Clips.absolute(SteerConfig.GLOBAL.calibration);
  }

  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    return Optional.of(SteerPutEvent.createOn(//
        safety.apply(signal.apply(RealScalar.of(timing.seconds())) //
            .multiply(HapticSteerConfig.GLOBAL.prbs7AmplitudeTorque))));
  }

  @Override // from AbstractModule
  protected void first() {
    timing.start();
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    timing.stop();
    SteerSocket.INSTANCE.removePutProvider(this);
  }
}
