// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owly.data.Stopwatch;
import ch.ethz.idsc.retina.dev.rimo.PIRimoRateController;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
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
  PIRimoRateController piL = new PIRimoRateController();
  Stopwatch stopwatch = Stopwatch.started();

  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(this);
  }

  private RimoGetEvent rimoGetEvent = null;

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    if (Objects.isNull(rimoGetEvent))
      return Optional.empty();
    // ---
    Scalar seconds = RealScalar.of(stopwatch.display_seconds());
    Scalar remaind = Mod.function(HALF_PERIOD.multiply(RealScalar.of(2))).apply(seconds);
    boolean isPassive = Scalars.lessThan(remaind, HALF_PERIOD);
    // int torque = isPassive ? 0 : 120;
    // ---
    Scalar tor = RealScalar.ZERO;
    try {
      Scalar vel_target = Quantity.of(isPassive ? 0 : 50, RimoGetTire.RATE_UNIT);
      Scalar vel_measur = rimoGetEvent.getTireL.getAngularRate();
      Scalar vel_error = vel_target.subtract(vel_measur);
      vel_error = QuantityMagnitude.SI().in(RimoGetTire.RATE_UNIT).apply(vel_error);
      // System.out.println(vel_error);
      tor = piL.iterate(vel_error);
      System.out.println(vel_error + " " + tor);
    } catch (Exception exception) {
      // ---
    }
    RimoPutTire putL = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, tor.number().shortValue());
    RimoPutEvent rimoPutEvent = new RimoPutEvent(putL, RimoPutTire.STOP);
    return Optional.of(rimoPutEvent);
  }

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }
}
