// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerDuo;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Mod;

/** module to test rimo torque control for a given target speed
 * class was used for tuning the PI controller in the workshop */
class RimoMetronomeModule extends AbstractModule implements RimoPutProvider {
  public static final Scalar HALF_PERIOD = RealScalar.of(2);
  // ---
  private final Timing timing = Timing.started();
  private final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerDuo();
  private final Scalar testPulseLo = Quantity.of(0, "rad*s^-1");
  private final Scalar testPulseHi = Quantity.of(20, "rad*s^-1");

  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addGetListener(rimoRateControllerWrap);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(rimoRateControllerWrap);
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    Scalar seconds = RealScalar.of(timing.seconds());
    Scalar remaind = Mod.function(HALF_PERIOD.multiply(RealScalar.of(2))).apply(seconds);
    boolean isPassive = Scalars.lessThan(remaind, HALF_PERIOD);
    // ---
    return rimoRateControllerWrap.iterate(isPassive ? testPulseLo : testPulseHi, RealScalar.ZERO);
  }
}
