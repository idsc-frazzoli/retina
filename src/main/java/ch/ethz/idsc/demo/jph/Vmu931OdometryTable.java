// code by jph
package ch.ethz.idsc.demo.jph;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.slam.Vmu931Odometry;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class Vmu931OdometryTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Vmu931Odometry vmu931Odometry = new Vmu931Odometry();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.VMU931_AG)) {
      Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
      vmu931Odometry.vmu931ImuFrame(vmu931ImuFrame);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
