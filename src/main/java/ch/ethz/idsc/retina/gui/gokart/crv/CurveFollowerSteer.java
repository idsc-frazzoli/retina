// code by jph
package ch.ethz.idsc.retina.gui.gokart.crv;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.util.StartAndStoppable;

class CurveFollowerSteer implements StartAndStoppable, SteerPutProvider {
  @Override // from StartAndStoppable
  public void start() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from StartAndStoppable
  public void stop() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    return Optional.empty(); // TODO
  }

  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
