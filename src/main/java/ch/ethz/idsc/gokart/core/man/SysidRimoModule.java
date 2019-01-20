// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** module generates ... */
/* package */ class SysidRimoModule extends AbstractModule implements PutProvider<RimoPutEvent> {
  private static final Scalar MAGNITUDE = Quantity.of(1500, NonSI.ARMS);
  // ---
  private final ManualControlProvider joystickLcmProvider = ManualConfig.GLOBAL.createProvider();
  private final Timing timing = Timing.started();
  private ScalarUnaryOperator signal = SysidSignals.CHIRP_SLOW.get();

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmProvider.start();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    joystickLcmProvider.stop();
  }

  /** @param signal */
  /* package */ void setSignal(ScalarUnaryOperator signal) {
    this.signal = signal;
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override // from PutProvider
  public Optional<RimoPutEvent> putEvent() {
    Optional<ManualControlInterface> optional = joystickLcmProvider.getManualControl();
    if (optional.isPresent())
      return fromJoystick(optional.get());
    return Optional.empty();
  }

  /* package */ Optional<RimoPutEvent> fromJoystick(ManualControlInterface manualControlInterface) {
    if (manualControlInterface.isAutonomousPressed()) {
      Scalar aheadAverage = manualControlInterface.getAheadAverage();
      Scalar timestamp = DoubleScalar.of(timing.seconds());
      return Optional.of(create(aheadAverage, timestamp));
    }
    return Optional.empty();
  }

  /** @param aheadAverage unitless in the interval [-1, 1]
   * @param timestamp
   * @return */
  /* package */ RimoPutEvent create(Scalar aheadAverage, Scalar timestamp) {
    short arms_raw = Magnitude.ARMS.toShort( //
        signal.apply(timestamp).multiply(aheadAverage).multiply(MAGNITUDE));
    return RimoPutHelper.operationTorque( //
        (short) -arms_raw, // sign left invert
        (short) +arms_raw // sign right id
    );
  }
}
