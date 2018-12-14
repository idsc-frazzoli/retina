// code by jph
package ch.ethz.idsc.gokart.dev;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrames;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.util.data.TimedFuse;
import ch.ethz.idsc.tensor.Scalar;

public class LabjackAdcLcmClient extends BinaryLcmClient {
  /** if no message is received for a period of 0.2[s]
   * the labjack adc frame is set to passive */
  private final TimedFuse timedFuse = new TimedFuse(0.2); // units in [s]
  private LabjackAdcFrame labjackAdcFrame = LabjackAdcFrames.PASSIVE;

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    labjackAdcFrame = new LabjackAdcFrame(byteBuffer);
    timedFuse.pacify();
  }

  @Override
  protected String channel() {
    return GokartLcmChannel.LABJACK_U3_ADC;
  }

  public Scalar getAheadSigned() {
    if (timedFuse.isBlown())
      labjackAdcFrame = LabjackAdcFrames.PASSIVE;
    return labjackAdcFrame.getAheadSigned();
  }
}
