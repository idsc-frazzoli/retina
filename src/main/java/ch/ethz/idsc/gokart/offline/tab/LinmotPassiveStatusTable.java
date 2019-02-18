// code by jph
// modified by mheim
// also get data if not fused
// only get GET data
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

public class LinmotPassiveStatusTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          RealScalar.of(linmotGetEvent.status_word), //
          RealScalar.of(linmotGetEvent.state_variable), //
          RealScalar.of(linmotGetEvent.actual_position), //
          RealScalar.of(linmotGetEvent.demand_position), //
          linmotGetEvent.getWindingTemperature1().map(Magnitude.DEGREE_CELSIUS).map(Round._1), //
          linmotGetEvent.getWindingTemperature2().map(Magnitude.DEGREE_CELSIUS).map(Round._1) //
      );
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
