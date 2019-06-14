// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.channel.VelodynePosChannel;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class GpsPresent implements OfflineLogListener {
  private static final Scalar MAX = Quantity.of(30.0, SI.SECOND);
  // ---
  private boolean gpsPresent = false;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(VelodynePosChannel.INSTANCE.channel())) {
      gpsPresent = true;
      throw OfflineLogPlayer.endOfFile();
    }
    if (Scalars.lessThan(MAX, time))
      throw OfflineLogPlayer.endOfFile();
  }

  public static void main(String[] args) {
    String prev = "";
    for (GokartLogFile glf : GokartLogFile.values()) {
      String next = glf.name().substring(1, 9);
      if (!next.equals(prev)) {
        File file = DatahakiLogFileLocator.file(glf);
        GpsPresent gpsPresent = new GpsPresent();
        try {
          OfflineLogPlayer.process(file, gpsPresent);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
        System.out.println(glf + "  " + gpsPresent.gpsPresent);
        prev = next;
      }
    }
  }
}
