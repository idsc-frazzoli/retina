// code by jph
package ch.ethz.idsc.gokart.offline.report;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public class LogFileSummary implements OfflineLogListener {
  // TODO max gyro rate
  // TODO battery level begin and end
  /** @param file lcm file
   * @throws IOException */
  public static LogFileSummary of(File file) throws IOException {
    LogFileSummary logFileSummary = new LogFileSummary();
    OfflineLogPlayer.process(file, logFileSummary);
    return logFileSummary;
  }

  private Scalar timePoseFirst = null;

  private LogFileSummary() {
    // ---
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gpe = new GokartPoseEvent(byteBuffer);
      if (Objects.isNull(timePoseFirst) && Scalars.nonZero(gpe.getQuality())) {
        timePoseFirst = time;
      }
    }
  }

  public Optional<Scalar> getLocalizationStart() {
    return Optional.ofNullable(timePoseFirst);
  }
}
