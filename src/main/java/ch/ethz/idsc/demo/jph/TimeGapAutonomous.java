// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.channel.SteerGetChannel;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class TimeGapAutonomous implements OfflineLogListener {
  private boolean fused = false;
  private Scalar max = Quantity.of(0, SI.SECOND);
  private Scalar prev = null;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (SteerGetChannel.INSTANCE.channel().equals(channel)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      fused = steerGetEvent.isActive();
    }
    if (fused)
      max = Max.of(max, time.subtract(prev));
    prev = time;
  }

  // ---
  public static void main(String[] args) throws IOException {
    for (GokartLogFile gokartLogFile : GokartLogFile.values())
      if (GokartLogFile._20190506T101748_99afbf25.ordinal() <= gokartLogFile.ordinal()) {
        System.out.println(gokartLogFile);
        File file = DatahakiLogFileLocator.INSTANCE.getAbsoluteFile(gokartLogFile);
        TimeGapAutonomous timeGapAutonomous = new TimeGapAutonomous();
        OfflineLogPlayer.process(file, timeGapAutonomous);
        System.out.println(timeGapAutonomous.max.map(Round._3));
      }
  }
}
