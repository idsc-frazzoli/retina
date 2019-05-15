// code by am 
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class NoFrictionExperiment extends AbstractModule implements SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final Timing timing = Timing.started();

  @Override
  protected void first() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }
 // the applied torque goes slowly from -0.7 to +0.7 and back to -0.7
  @Override // from PutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      double timeDouble = timing.seconds();
      Scalar torqueStep = Quantity.of(0.015, "SCT*s^-1");
      Scalar appliedTorque = Quantity.of(-0.7, "SCT");
      while (timeDouble < 188) {
        timeDouble += 1.0;
        Scalar time = Quantity.of(timeDouble, SI.SECOND);
        if (timeDouble < 94) {
          appliedTorque = appliedTorque.add(torqueStep.multiply(time));
        }
        if (timeDouble > 93) {
          appliedTorque = appliedTorque.subtract(torqueStep.multiply(time));
        }
        return Optional.of(SteerPutEvent.createOn(appliedTorque));
      }
    }
    return Optional.empty();
  }
}
