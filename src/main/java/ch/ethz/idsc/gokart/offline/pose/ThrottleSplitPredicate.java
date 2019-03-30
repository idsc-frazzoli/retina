// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.u3.GokartLabjackFrame;
import ch.ethz.idsc.gokart.lcm.LogSplitPredicate;
import ch.ethz.idsc.gokart.offline.channel.LabjackAdcChannel;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** triggers once when throttle is pressed above threshold */
public class ThrottleSplitPredicate implements LogSplitPredicate {
  private static final Scalar THRESHOLD = RealScalar.of(0.2);
  // ---
  private boolean isActive = true;

  @Override // from LogSplitPredicate
  public boolean split(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (LabjackAdcChannel.INSTANCE.channel().equals(channel) && isActive) {
      ManualControlInterface manualControlInterface = new GokartLabjackFrame(byteBuffer);
      if (Scalars.lessThan(THRESHOLD, manualControlInterface.getAheadAverage())) {
        isActive = false;
        return true;
      }
    }
    return false;
  }
}
