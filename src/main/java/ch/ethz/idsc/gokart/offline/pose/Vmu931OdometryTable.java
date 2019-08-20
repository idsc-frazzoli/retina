// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.slam.Vmu931Odometry;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.pose.VelocityHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

public class Vmu931OdometryTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  public final Vmu931Odometry vmu931Odometry = new Vmu931Odometry(SensorsConfig.GLOBAL.getPlanarVmu931Imu());
  private final Scalar time_min;
  private boolean flag = false;

  public Vmu931OdometryTable(Scalar time_min) {
    this.time_min = time_min;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (flag || Scalars.lessEquals(time_min, time))
      if (channel.equals(GokartLcmChannel.VMU931_AG)) {
        flag = true;
        Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
        vmu931Odometry.vmu931ImuFrame(vmu931ImuFrame);
        tableBuilder.appendRow( //
            Magnitude.SECOND.apply(time).map(Round._6), //
            PoseHelper.toUnitless(vmu931Odometry.getPose()).map(Round._6), //
            VelocityHelper.toUnitless(vmu931Odometry.getVelocity()).map(Round._6));
      }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
