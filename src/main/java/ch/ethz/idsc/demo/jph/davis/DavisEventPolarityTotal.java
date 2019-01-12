// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** class adapted for gioele
 * extract all dvs events */
class DavisEventPolarityTotal implements OfflineLogListener, DavisDvsListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  int[] total = new int[2];

  public DavisEventPolarityTotal() {
    davisDvsDatagramDecoder.addDvsListener(this);
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // System.out.println(davisDvsEvent.toString());
    ++total[davisDvsEvent.i];
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("#0=" + total[0] + "\n");
    stringBuilder.append("#1=" + total[1] + "\n");
    return stringBuilder.toString();
  }

  public static void main(String[] args) throws IOException {
    File file = HomeDirectory.file("gokart/twist/20180108T165210_4/log.lcm");
    file = HomeDirectory.file("gokart/pursuit/20180307T154859/log.lcm");
    DavisEventPolarityTotal oll = new DavisEventPolarityTotal();
    oll.davisDvsDatagramDecoder.addDvsListener(oll);
    OfflineLogPlayer.process(file, oll);
    System.out.println(oll.toString());
  }
}
