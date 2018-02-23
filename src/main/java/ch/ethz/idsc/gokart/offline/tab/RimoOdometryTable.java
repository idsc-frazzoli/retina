// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.red.Mean;

public class RimoOdometryTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  // ---

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rge = new RimoGetEvent(byteBuffer);
      Tensor rates = rge.getAngularRate_Y_pair();
      Scalar speed = Mean.of(rates).multiply(ChassisGeometry.GLOBAL.tireRadiusRear).Get();
      // rad/s * m == (m / s) / m
      Scalar rate = Differences.of(rates).Get(0) //
          .multiply(RationalScalar.HALF) //
          .multiply(ChassisGeometry.GLOBAL.tireRadiusRear) //
          .divide(ChassisGeometry.GLOBAL.yTireRear);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND), //
          speed.map(Magnitude.VELOCITY), //
          rate.map(Magnitude.ANGULAR_RATE) //
      );
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
