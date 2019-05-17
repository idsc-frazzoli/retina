// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public final class GokartLabjackLcmClient extends BinaryLcmClient implements ManualControlProvider, StartAndStoppable {
  private static final Scalar HALF = RealScalar.of(0.5);
  /** if no message is received for a period of 0.2[s]
   * the labjack adc frame is set to passive */
  private final Watchdog watchdog;
  // ---
  private Tensor prevFrame = GokartLabjackFrame.ARRAY_PASSIVE;
  private ManualControlInterface manualControlInterface = null;

  /** @param channel
   * @param timeout with unit seconds [s] */
  public GokartLabjackLcmClient(String channel, Scalar timeout) {
    super(channel);
    watchdog = SoftWatchdog.notified(Magnitude.SECOND.toDouble(timeout));
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    Tensor nextFrame = new LabjackAdcFrame(byteBuffer).asVector();
    manualControlInterface = // moving average of width 2
        new GokartLabjackFrame(nextFrame.add(prevFrame).multiply(HALF));
    prevFrame = nextFrame;
    watchdog.notifyWatchdog();
  }

  @Override
  public void start() {
    startSubscriptions();
  }

  @Override
  public void stop() {
    stopSubscriptions();
  }

  @Override
  public Optional<ManualControlInterface> getManualControl() {
    return Optional.ofNullable(watchdog.isBarking() //
        ? null
        : manualControlInterface);
  }
}
