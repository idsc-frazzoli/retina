package ch.ethz.idsc.gokart.report;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;

public class LogFileSummary implements OfflineLogListener {
  // TODO max gyro rate
  // TODO battery level begin and end
  /** @param file lcm file
   * @throws IOException */
  public static void of(File file) throws IOException {
    LogFileSummary logFileSummary = new LogFileSummary();
    OfflineLogPlayer.process(file, logFileSummary);
  }

  public LogFileSummary() {
  }

  GokartPoseEvent gpe;

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gpe = new GokartPoseEvent(byteBuffer);
    }
  }
}
