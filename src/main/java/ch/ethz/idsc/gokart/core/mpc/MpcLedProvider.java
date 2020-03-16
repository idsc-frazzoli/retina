// code by ta
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.led.LEDPutEvent;
import ch.ethz.idsc.gokart.dev.led.LEDPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ final class MpcLedProvider implements LEDPutProvider {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final Timing timing;
  private final MPCSteering mpcSteering;
  private final MpcLedFunction mpcLedFunction = MpcLedFunction.DETAILED;

  public MpcLedProvider(Timing timing, MPCSteering mpcSteering) {
    this.mpcSteering = mpcSteering;
    this.timing = timing;
  }

  @Override // from PutProvider
  public Optional<LEDPutEvent> putEvent() {
    Scalar time = Quantity.of(timing.seconds(), SI.SECOND);
    return mpcSteering.getSteering(time).map(this::angleLED); // use steering angle
  }

  private LEDPutEvent angleLED(Tensor steering) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    return LEDPutEvent.from(mpcLedFunction.apply(steering.Get(0), currAngle));
  }

  public final ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
