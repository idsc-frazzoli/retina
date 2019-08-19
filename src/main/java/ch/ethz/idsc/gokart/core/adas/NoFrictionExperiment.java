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
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.sca.Clips;

/** applies sawtooth torque signal to steering
 * 
 * <p>the experiment was conducted with no-slip surface pads in order
 * to estimate the restoring force at various steering angles. */
public final class NoFrictionExperiment extends AbstractModule implements SteerPutProvider {
  private static final Scalar SAMPLE_DURATION = Quantity.of(30, SI.SECOND);
  // ---
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final Timing timing = Timing.started();
  /** the applied torque goes slowly to 0.7 and -0.7 and back to 0 */
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

  @Override // from PutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy())
      return Optional.of(SteerPutEvent.createOn(time2torque(Quantity.of(timing.seconds(), SI.SECOND))));
    return Optional.empty();
  }

  /** @param time with unit [s]
   * @return */
  /* package */ Scalar time2torque(Scalar time) {
    Scalar scalar = Clips.positive(signal.length() - 1).apply(time.divide(SAMPLE_DURATION));
    return interpolation.At(scalar);
  }
}
