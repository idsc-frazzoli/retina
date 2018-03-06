// code by jph
package ch.ethz.idsc.demo.yt.prc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;

public class NewTrajectoryProcess implements OfflineLogListener {
  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("gokart.status.get")) {
      SteerColumnInterface gse = new GokartStatusEvent(byteBuffer);
      System.out.println(time + " " + gse.getSteerColumnEncoderCentered());
    }
  }

  public static void main(String[] args) throws IOException {
    File file = UserHome.file("gokart/pursuit/20180305T170018/log.lcm");
    OfflineLogListener oll = new NewTrajectoryProcess();
    OfflineLogPlayer.process(file, oll);
  }
}
