// code by jph
package ch.ethz.idsc.gokart.dev;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickAdapter;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrames;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.util.data.TimedFuse;
import ch.ethz.idsc.tensor.Scalar;

public final class LabjackAdcLcmClient extends BinaryLcmClient implements ManualControlProvider {
  private final String channel;
  /** if no message is received for a period of 0.2[s]
   * the labjack adc frame is set to passive */
  private final TimedFuse timedFuse; // units in [s]
  private LabjackAdcFrame labjackAdcFrame = LabjackAdcFrames.PASSIVE;

  /** @param channel
   * @param timeout in [s] */
  public LabjackAdcLcmClient(String channel, double timeout) {
    this.channel = channel;
    timedFuse = new TimedFuse(timeout);
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    labjackAdcFrame = new LabjackAdcFrame(byteBuffer);
    timedFuse.pacify();
  }

  @Override
  protected String channel() {
    return channel;
  }

  public Scalar getAheadSigned() {
    if (timedFuse.isBlown())
      labjackAdcFrame = LabjackAdcFrames.PASSIVE;
    return labjackAdcFrame.getAheadSigned();
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
  public Optional<GokartJoystickInterface> getJoystick() {
    return Optional.of(GokartJoystickAdapter.PASSIVE);
  }
}
