// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

//TODO: configurable
//TODO: comment appropriately
/** sends stop command if linmot status is "not-operational"
 * {@link LinmotGetEvent#isOperational()}
 * 
 * <p>The module has {@link ProviderRank#SAFETY} to prevent the autonomous
 * drive without operational brake. */
public final class SpeedLimitSafetyModule extends AbstractModule implements RimoGetListener, PutProvider<RimoPutEvent> {
  private boolean limitObeyed = false;

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

  @Override // from LinmotGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    Scalar maxSpeed = Max.of(//
        rimoGetEvent.getTireL.getAngularRate_Y().abs(), //
        rimoGetEvent.getTireR.getAngularRate_Y().abs());
    limitObeyed = Scalars.lessThan(maxSpeed, Quantity.of(10, SI.PER_SECOND));
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.SAFETY;
  }

  @Override // from PutProvider
  public Optional<RimoPutEvent> putEvent() {
    return limitObeyed //
        ? Optional.empty()
        : StaticHelper.OPTIONAL_RIMO_PASSIVE;
  }
}
