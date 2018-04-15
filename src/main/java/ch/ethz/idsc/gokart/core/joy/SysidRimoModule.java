// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.math.PRBS7Signal;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** module generates ... */
public class SysidRimoModule extends AbstractModule implements PutProvider<RimoPutEvent> {
  private static final Scalar[] VALUE = new Scalar[] { RealScalar.of(-1), RealScalar.of(+1) };
  private static final Scalar MAGNITUDE = RealScalar.of(1500); // TODO magic const, unit [ARMS]
  private static final Scalar PERIOD = RealScalar.of(.3); // TODO magic const, unit [s]
  // ---
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private final Stopwatch stopwatch = Stopwatch.started();
  private final PRBS7Signal prbs7Signal = new PRBS7Signal(PERIOD);

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    joystickLcmClient.stopSubscriptions();
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override // from PutProvider
  public Optional<RimoPutEvent> putEvent() {
    Optional<JoystickEvent> joystick = joystickLcmClient.getJoystick();
    if (joystick.isPresent()) {
      GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
      if (gokartJoystickInterface.isAutonomousPressed()) {
        Scalar aheadAverage = gokartJoystickInterface.getAheadAverage();
        Scalar timestamp = DoubleScalar.of(stopwatch.display_seconds());
        return Optional.of(create(aheadAverage, timestamp));
      }
    }
    return Optional.empty();
  }

  /* package */ RimoPutEvent create(Scalar aheadAverage, Scalar timestamp) {
    Scalar bit = prbs7Signal.apply(timestamp); // 0 or 1
    Scalar value = VALUE[bit.number().intValue()]; // -1 or 1
    value = value.multiply(aheadAverage).multiply(MAGNITUDE);
    short armsL_raw = (short) (-value.number().shortValue()); // sign left invert
    short armsR_raw = (short) (+value.number().shortValue()); // sign right id
    return new RimoPutEvent( //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsL_raw), //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsR_raw) //
    );
  }
}
