// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class RimoOdometryTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  // ---

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rge = new RimoGetEvent(byteBuffer);
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rge);
      Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rge);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND), //
          speed.map(Magnitude.VELOCITY), //
          rate.map(Magnitude.PER_SECOND) //
      );
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
