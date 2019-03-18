// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.slam.Vmu931Odometry;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class Vmu931OdometryTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Vmu931Odometry vmu931Odometry = new Vmu931Odometry(SensorsConfig.getPlanarVmu931Imu());

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.VMU931_AG)) {
      Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
      vmu931Odometry.vmu931ImuFrame(vmu931ImuFrame);
      tableBuilder.appendRow( //
          Magnitude.SECOND.apply(time), //
          GokartPoseHelper.toUnitless(vmu931Odometry.inertialOdometry.getPose()));
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/data/gokart/cuts/20190314/20190314T154544_18");
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    File file = new File(folder, "post.lcm");
    Vmu931OdometryTable vmu931OdometryTable = new Vmu931OdometryTable();
    vmu931OdometryTable.vmu931Odometry.inertialOdometry.resetPose(gokartLogInterface.pose());
    OfflineLogPlayer.process(file, vmu931OdometryTable);
    Export.of(HomeDirectory.file("vmu931_odometry.csv"), vmu931OdometryTable.getTable().map(CsvFormat.strict()));
  }
}
