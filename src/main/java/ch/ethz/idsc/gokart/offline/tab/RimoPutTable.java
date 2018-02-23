// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

public class RimoPutTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoPutEvent rimoGetEvent = RimoPutHelper.from(byteBuffer);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          rimoGetEvent.putL.vector_raw(), //
          rimoGetEvent.putR.vector_raw() //
      );
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
