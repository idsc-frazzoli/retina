// code by jph
package ch.ethz.idsc.retina.gui.gokart.crv;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

class CurveFollowerSteer extends CurveFollowerBase implements SteerPutProvider {
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
    if (isOperational()) {
    }
    return Optional.empty(); // TODO
  }

  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
