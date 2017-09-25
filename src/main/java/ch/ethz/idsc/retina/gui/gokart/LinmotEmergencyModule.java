// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** sends stop command if either winding temperature is outside valid range */
public class LinmotEmergencyModule extends AbstractModule implements LinmotGetListener, RimoPutProvider {
  /** degree celsius */
  // TODO NRJ check valid range
  private static final Clip TEMPERATURE_RANGE = Clip.function( //
      Quantity.of(2, "C"), //
      Quantity.of(110, "C"));
  // ---
  private boolean flag = false;

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removeProvider(this);
    LinmotSocket.INSTANCE.removeGetListener(this);
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> getPutEvent() {
    return Optional.ofNullable(flag ? RimoPutEvent.STOP : null);
  }

  @Override
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    {
      Scalar temperature = linmotGetEvent.getWindingTemperature1();
      flag |= !TEMPERATURE_RANGE.apply(temperature).equals(temperature);
    }
    {
      Scalar temperature = linmotGetEvent.getWindingTemperature2();
      flag |= !TEMPERATURE_RANGE.apply(temperature).equals(temperature);
    }
  }
}
