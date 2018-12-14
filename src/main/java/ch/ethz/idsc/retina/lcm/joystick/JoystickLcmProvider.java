// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickAdapter;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.util.data.TimedFuse;

/** client to lcm channel with joystick information */
public final class JoystickLcmProvider extends BinaryLcmClient implements ManualControlProvider {
  private final String channel;
  private final TimedFuse timedFuse;
  // ---
  private GokartJoystickInterface joystickEvent = GokartJoystickAdapter.PASSIVE;

  /** @param channel for instance "generic_xbox_pad"
   * @param timeout_ms maximum age of joystick information relayed to application layer */
  public JoystickLcmProvider(String channel, double timeout) {
    this.channel = channel;
    timedFuse = new TimedFuse(timeout);
  }

  /** @return recent joystick readout, or empty */
  @Override
  public Optional<GokartJoystickInterface> getJoystick() {
    if (timedFuse.isBlown())
      return Optional.empty();
    return Optional.of(joystickEvent);
  }

  @Override // from LcmClientAdapter
  protected String channel() {
    return channel;
  }

  @Override // from LcmClientAdapter
  protected void messageReceived(ByteBuffer byteBuffer) {
    timedFuse.pacify();
    joystickEvent = (GokartJoystickInterface) JoystickDecoder.decode(byteBuffer);
  }

  @Override
  public void start() {
    startSubscriptions();
  }

  @Override
  public void stop() {
    stopSubscriptions();
  }
}
