// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.DavisImuTable;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicMeanFilter;
import ch.ethz.idsc.owl.subdiv.curve.Se2Geodesic;
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

  public static void doit(String title, File lcmfile) throws IOException {
    PoseFiltering poseFiltering = new PoseFiltering();
    DavisImuTable davisImuTable = DavisImuTable.all();
    OfflineLogPlayer.process(lcmfile, poseFiltering, davisImuTable);
    File dir = new File(UserHome.file("odometry"), title);
    dir.mkdir();
    Export.of( //
        new File(dir, "pose.csv"), //
        poseFiltering.getTable().map(CsvFormat.strict()));
    Export.of( //
        new File(dir, "imu.csv"), //
        davisImuTable.getTable().map(CsvFormat.strict()));
    Tensor table = poseFiltering.getTable().copy();
    Tensor pose = Tensor.of(table.stream().map(r -> r.extract(1, 4)));
    Stopwatch stopwatch = Stopwatch.started();
    GeodesicMeanFilter geodesicMeanFilter = new GeodesicMeanFilter(Se2Geodesic.INSTANCE, 15);
    // geodesicMeanFilter = new GeodesicMeanFilter(RnGeodesic.INSTANCE, 5);
    pose = geodesicMeanFilter.apply(pose);
    System.out.println("filter=" + stopwatch.display_seconds());
    table.set(pose.get(Tensor.ALL, 0), Tensor.ALL, 1);
    table.set(pose.get(Tensor.ALL, 1), Tensor.ALL, 2);
    table.set(pose.get(Tensor.ALL, 2), Tensor.ALL, 3);
    File file2 = new File(dir, "posefiltered.csv");
    System.out.println(file2);
    Export.of( //
        file2, //
        table.map(CsvFormat.strict()));
  }

  public static void main(String[] args) throws IOException {
    doit("20180820T143852_1", UserHome.file("20180820T143852_1.lcm"));
    // File directory = new File("/media/datahaki/media/ethz/gokart/topic/odometry");
    //// directory = new File("/media/datahaki/media/ethz/gokart/topic/track_white");
    // for (File file : directory.listFiles())
    // try {
    // String title = file.getName();
    // System.out.println(title);
    // File lcmfile = new File(file, "log.lcm");
    // doit(title, lcmfile);
    // } catch (Exception exception) {
    // exception.printStackTrace();
    // }
  }
}
