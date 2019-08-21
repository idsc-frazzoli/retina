// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

/** safety module makes steering passive
 * when driver is pushing the brake
 * 
 * the state lasts until the brake is recalibrated */
public final class SteerPassiveModule extends AbstractModule implements LinmotGetListener, PutProvider<SteerPutEvent> {
  /** threshold that determines that driver is pushing the brake */
  private static final Scalar ACTUAL_POSITION_PRESSED = Quantity.of(-0.02, SI.METER);
  // ---
  private boolean isHealthy = true;

  @Override
  protected void first() {
    LinmotSocket.INSTANCE.addGetListener(this);
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(this);
    LinmotSocket.INSTANCE.removeGetListener(this);
  }

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    if (linmotGetEvent.isOperational())
      isHealthy = true; // the software is controlling the brake
    else
      isHealthy &= Scalars.lessThan(ACTUAL_POSITION_PRESSED, linmotGetEvent.getActualPosition());
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.SAFETY;
  }

  @Override // from PutProvider
  public Optional<SteerPutEvent> putEvent() {
    return isHealthy //
        ? Optional.empty()
        : Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0);
  }
}
