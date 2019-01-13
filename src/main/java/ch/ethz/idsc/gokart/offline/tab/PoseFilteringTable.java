// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class PoseFilteringTable implements OfflineTableSupplier {
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

  /** @param lcmfile
   * @param dest
   * @throws IOException */
  public static void process(File lcmfile, File dest) throws IOException {
    PoseFilteringTable poseFiltering = new PoseFilteringTable();
    DavisImuTable davisImuTable = DavisImuTable.all();
    OfflineLogPlayer.process(lcmfile, poseFiltering, davisImuTable);
    dest.mkdir();
    Export.of( //
        new File(dest, "pose.csv"), //
        poseFiltering.getTable().map(CsvFormat.strict()));
    Export.of( //
        new File(dest, "imu.csv"), //
        davisImuTable.getTable().map(CsvFormat.strict()));
    Tensor table = poseFiltering.getTable().copy();
    Tensor pose = Tensor.of(table.stream().map(r -> r.extract(1, 4)));
    Timing timing = Timing.started();
    TensorUnaryOperator geodesicMeanFilter = //
        GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), 7);
    pose = geodesicMeanFilter.apply(pose);
    System.out.println("filter=" + timing.seconds());
    table.set(pose.get(Tensor.ALL, 0), Tensor.ALL, 1);
    table.set(pose.get(Tensor.ALL, 1), Tensor.ALL, 2);
    table.set(pose.get(Tensor.ALL, 2), Tensor.ALL, 3);
    File file2 = new File(dest, "posefiltered.csv");
    System.out.println(file2);
    Export.of( //
        file2, //
        table.map(CsvFormat.strict()));
  }
}
