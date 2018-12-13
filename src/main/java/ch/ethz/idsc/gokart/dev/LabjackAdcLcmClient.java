// code by jph
package ch.ethz.idsc.gokart.dev;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.util.data.TimedFuse;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class LabjackAdcLcmClient extends BinaryLcmClient {
  private final TimedFuse timedFuse = new TimedFuse(0.3);
  private LabjackAdcFrame labjackAdcFrame;

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(byteBuffer);
    this.labjackAdcFrame = labjackAdcFrame;
    // System.out.println("recevied");
    timedFuse.pacify();
  }

  @Override
  protected String channel() {
    return GokartLcmChannel.LABJACK_U3_ADC;
  }

  public Scalar getAhead() {
    // System.out.println("get ahead");
    if (timedFuse.isBlown())
      return RealScalar.ZERO;
    return labjackAdcFrame.getAheadSigned();
  }
}
