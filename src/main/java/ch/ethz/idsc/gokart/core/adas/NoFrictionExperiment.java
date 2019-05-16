// code by am 
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.sca.Clips;

public class NoFrictionExperiment extends AbstractModule implements SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final Timing timing = Timing.started();
  private final Tensor signal = //
      QuantityTensor.of(Tensors.vector(0, 0.7, 0, -0.7, 0), SteerPutEvent.UNIT_RTORQUE);
  private final Interpolation interpolation = LinearInterpolation.of(signal);

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
    if (steerColumnTracker.isCalibratedAndHealthy())
      return Optional.of(SteerPutEvent.createOn(time2torque(timing.seconds())));
    return Optional.empty();
  }

  /* package */ Scalar time2torque(double timeDouble) {
    Scalar scalar = Clips.interval(0, signal.length() - 1).apply(RealScalar.of(timeDouble / 30));
    return interpolation.At(scalar);
  }
}
