// code by ynager
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.ArrayFloatBlob;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

public class TrajectoryTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    System.out.print("Processing: " + time + "\n");
    if (channel.equals(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME)) {
      Tensor trj = ArrayFloatBlob.decode(byteBuffer);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          trj.map(Round._6));
      System.out.println(tableBuilder.getRowCount());
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}