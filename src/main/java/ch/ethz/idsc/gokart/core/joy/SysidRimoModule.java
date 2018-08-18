// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** module generates ... */
/* package */ class SysidRimoModule extends AbstractModule implements PutProvider<RimoPutEvent> {
  private static final Scalar MAGNITUDE = Quantity.of(1500, NonSI.ARMS);
  // ---
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
  private final Stopwatch stopwatch = Stopwatch.started();
  private ScalarUnaryOperator signal = SysidSignals.CHIRP_SLOW.get();

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmProvider.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    joystickLcmProvider.stopSubscriptions();
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
    Optional<JoystickEvent> joystick = joystickLcmProvider.getJoystick();
    if (joystick.isPresent())
      return fromJoystick((GokartJoystickInterface) joystick.get());
    return Optional.empty();
  }

  /* package */ Optional<RimoPutEvent> fromJoystick(GokartJoystickInterface gokartJoystickInterface) {
    if (gokartJoystickInterface.isAutonomousPressed()) {
      Scalar aheadAverage = gokartJoystickInterface.getAheadAverage();
      Scalar timestamp = DoubleScalar.of(stopwatch.display_seconds());
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
