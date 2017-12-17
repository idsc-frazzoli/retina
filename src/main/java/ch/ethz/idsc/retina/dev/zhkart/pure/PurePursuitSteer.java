// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;

class PurePursuitSteer extends PurePursuitBase implements SteerPutProvider {
  @Override // from StartAndStoppable
  public void start() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from StartAndStoppable
  public void stop() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  private Scalar angle = DoubleScalar.of(0.0);

  public void setHeading(Scalar angle) {
    this.angle = angle;
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
