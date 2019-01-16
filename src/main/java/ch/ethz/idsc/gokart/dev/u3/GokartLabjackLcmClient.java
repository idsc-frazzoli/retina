// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.data.TimedFuse;
import ch.ethz.idsc.retina.util.data.WatchdogInterface;

public final class GokartLabjackLcmClient extends BinaryLcmClient implements ManualControlProvider {
  /** if no message is received for a period of 0.2[s]
   * the labjack adc frame is set to passive */
  private final WatchdogInterface watchdogInterface;
  // ---
  private ManualControlInterface manualControlInterface = null;

  /** @param channel
   * @param timeout in [s] */
  public GokartLabjackLcmClient(String channel, double timeout) {
    super(channel);
    watchdogInterface = TimedFuse.notified(timeout);
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    manualControlInterface = new GokartLabjackFrame(byteBuffer);
    watchdogInterface.notifyWatchdog();
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
    return Optional.ofNullable(watchdogInterface.isWatchdogBarking() //
        ? null
        : manualControlInterface);
  }
}
