// code by jph
package ch.ethz.idsc.gokart.dev;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.data.TimedFuse;
import ch.ethz.idsc.retina.util.data.WatchdogInterface;

/** client to lcm channel with joystick information */
/* package */ final class JoystickLcmProvider extends BinaryLcmClient implements ManualControlProvider {
  private final WatchdogInterface watchdogInterface;
  // ---
  private ManualControlInterface manualControlInterface = null;

  /** @param channel for instance "generic_xbox_pad"
   * @param timeout_ms maximum age of joystick information relayed to application layer */
  public JoystickLcmProvider(String channel, double timeout) {
    super(channel);
    watchdogInterface = TimedFuse.notified(timeout);
  }

  @Override // from LcmClientAdapter
  protected void messageReceived(ByteBuffer byteBuffer) {
    watchdogInterface.notifyWatchdog();
    manualControlInterface = (ManualControlInterface) JoystickDecoder.decode(byteBuffer);
  }

  @Override
  public void start() {
    startSubscriptions();
  }

  @Override
  public void stop() {
    stopSubscriptions();
  }

  /** @return recent joystick readout, or empty */
  @Override
  public Optional<ManualControlInterface> getManualControl() {
    return Optional.ofNullable(watchdogInterface.isWatchdogBarking() //
        ? null
        : manualControlInterface);
  }
}
