// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.DavisImuTable;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;

class PoseFiltering implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseInterface = new GokartPoseEvent(byteBuffer);
      Tensor pose = gokartPoseInterface.getPose();
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND), //
          GokartPoseHelper.toUnitless(pose), //
          gokartPoseInterface.getQuality());
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) {
    File directory = new File("/media/datahaki/media/ethz/gokart/topic/odometry");
    for (File file : directory.listFiles())
      try {
        String title = file.getName();
        System.out.println(title);
        PoseFiltering poseFiltering = new PoseFiltering();
        DavisImuTable davisImuTable = DavisImuTable.all();
        OfflineLogPlayer.process(new File(file, "log.lcm"), poseFiltering, davisImuTable);
        File dir = new File(UserHome.file("odometry"), title);
        dir.mkdir();
        Export.of( //
            new File(dir, "pose.csv"), //
            poseFiltering.getTable().map(CsvFormat.strict()));
        Export.of( //
            new File(dir, "imu.csv"), //
            davisImuTable.getTable().map(CsvFormat.strict()));
      } catch (Exception exception) {
        // ---
      }
  }
}
