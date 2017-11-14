// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owly.data.Stopwatch;
import ch.ethz.idsc.retina.dev.rimo.PIRimoRateController;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Mod;

/** module to test rimo torque control for a given target speed */
public class RimoMetronomeModule extends AbstractModule implements RimoPutProvider, RimoGetListener {
  public static final Scalar HALF_PERIOD = RealScalar.of(2);
  // ---
  private final Stopwatch stopwatch = Stopwatch.started();
  private final PIRimoRateController piL = new PIRimoRateController();
  private final PIRimoRateController piR = new PIRimoRateController();

  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(this);
  }

  private RimoGetEvent rimoGetEvent = null;

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    if (Objects.isNull(rimoGetEvent))
      return Optional.empty();
    // ---
    Scalar seconds = RealScalar.of(stopwatch.display_seconds());
    Scalar remaind = Mod.function(HALF_PERIOD.multiply(RealScalar.of(2))).apply(seconds);
    boolean isPassive = Scalars.lessThan(remaind, HALF_PERIOD);
    // ---
    short valueL = 0;
    {
      Scalar torque = Quantity.of(0, RimoPutTire.UNIT_TORQUE);
      try {
        Scalar vel_target = isPassive ? RimoConfig.GLOBAL.testPulseLo : RimoConfig.GLOBAL.testPulseHi;
        Scalar vel_measur = rimoGetEvent.getTireL.getAngularRate();
        Scalar vel_error = vel_target.subtract(vel_measur);
        torque = piL.iterate(vel_error);
      } catch (Exception exception) {
        // ---
      }
      valueL = QuantityMagnitude.singleton(RimoPutTire.UNIT_TORQUE).apply(torque).number().shortValue();
    }
    short valueR = 0;
    {
      Scalar torque = Quantity.of(0, RimoPutTire.UNIT_TORQUE);
      try {
        Scalar vel_target = isPassive ? RimoConfig.GLOBAL.testPulseLo : RimoConfig.GLOBAL.testPulseHi;
        Scalar vel_measur = rimoGetEvent.getTireR.getAngularRate();
        Scalar vel_error = vel_target.subtract(vel_measur);
        torque = piR.iterate(vel_error);
      } catch (Exception exception) {
        // ---
      }
      valueR = QuantityMagnitude.singleton(RimoPutTire.UNIT_TORQUE).apply(torque).number().shortValue();
    }
    RimoPutTire putL = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, valueL);
    RimoPutTire putR = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, valueR);
    RimoPutEvent rimoPutEvent = new RimoPutEvent(putL, putR);
    return Optional.of(rimoPutEvent);
  }
}
