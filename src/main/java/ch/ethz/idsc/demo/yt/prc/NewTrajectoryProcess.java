// code by jph
package ch.ethz.idsc.demo.yt.prc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;

/** class adapted for gioele
 * extract all dvs events */
class NewTrajectoryProcess implements OfflineLogListener, DavisDvsListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();

  public NewTrajectoryProcess() {
    davisDvsDatagramDecoder.addDvsListener(this);
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    System.out.println(channel);
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    System.out.println(davisDvsEvent.toString());
  }

  public static void main(String[] args) throws IOException {
    File file = UserHome.file("gokart/twist/20180108T165210_4/log.lcm");
    NewTrajectoryProcess oll = new NewTrajectoryProcess();
    oll.davisDvsDatagramDecoder.addDvsListener(oll);
    OfflineLogPlayer.process(file, oll);
  }
}
