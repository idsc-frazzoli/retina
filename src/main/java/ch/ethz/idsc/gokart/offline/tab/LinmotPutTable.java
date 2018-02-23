// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

public class LinmotPutTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(LinmotLcmServer.CHANNEL_PUT)) {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent(byteBuffer);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          linmotPutEvent.vector_raw() //
      );
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
