// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Mod;

/** module to test rimo torque control for a given target speed */
class RimoMetronomeModule extends AbstractModule implements RimoPutProvider {
  public static final Scalar HALF_PERIOD = RealScalar.of(2);
  // ---
  private final Stopwatch stopwatch = Stopwatch.started();
  private final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerWrap();

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
    Scalar seconds = RealScalar.of(stopwatch.display_seconds());
    Scalar remaind = Mod.function(HALF_PERIOD.multiply(RealScalar.of(2))).apply(seconds);
    boolean isPassive = Scalars.lessThan(remaind, HALF_PERIOD);
    // ---
    return rimoRateControllerWrap.iterate(Tensors.of( //
        isPassive ? RimoConfig.GLOBAL.testPulseLo : RimoConfig.GLOBAL.testPulseHi, //
        isPassive ? RimoConfig.GLOBAL.testPulseLo : RimoConfig.GLOBAL.testPulseHi));
  }
}
