// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.time.SoftWatchdog;
import ch.ethz.idsc.retina.util.time.Watchdog;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Quantity;

public final class GokartLabjackLcmClient extends BinaryLcmClient implements ManualControlProvider, StartAndStoppable {
  private static final Scalar HALF = RealScalar.of(0.5);
  /** if no message is received for a period of 0.2[s]
   * the labjack adc frame is set to passive */
  private final Watchdog watchdog;
  // ---
  private Tensor prevFrame = Array.of(i -> Quantity.of(0.0, SI.VOLT), 5);
  private ManualControlInterface manualControlInterface = null;

  /** @param channel
   * @param timeout with unit seconds [s] */
  public GokartLabjackLcmClient(String channel, Scalar timeout) {
    super(channel);
    watchdog = SoftWatchdog.notified(Magnitude.SECOND.toDouble(timeout));
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    Tensor nextFrame = new LabjackAdcFrame(byteBuffer).allADC();
    // average the button values on channels 0, 1, 3
    Tensor mean = nextFrame.add(prevFrame).multiply(HALF); // moving average of width 2
    // preserve the throttle value on channel 2
    mean.set(nextFrame.Get(GokartLabjackAdc.THROTTLE.ordinal()), GokartLabjackAdc.THROTTLE.ordinal());
    manualControlInterface = new GokartLabjackFrame(mean);
    prevFrame = nextFrame;
    watchdog.notifyWatchdog();
  }

  @Override // from StartAndStoppable
  public void start() {
    startSubscriptions();
  }

  @Override // from StartAndStoppable
  public void stop() {
    stopSubscriptions();
  }

  @Override // from ManualControlProvider
  public Optional<ManualControlInterface> getManualControl() {
    return Optional.ofNullable(watchdog.isBarking() //
        ? null
        : manualControlInterface);
  }
}
