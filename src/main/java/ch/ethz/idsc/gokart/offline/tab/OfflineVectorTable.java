// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.function.Function;

import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

public class OfflineVectorTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final String channel;
  private final Function<ByteBuffer, OfflineVectorInterface> function;

  public OfflineVectorTable(String channel, Function<ByteBuffer, OfflineVectorInterface> function) {
    this.channel = channel;
    this.function = function;
  }

  @Override // from OfflineLogListener
  public final void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (this.channel.equals(channel))
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          function.apply(byteBuffer).asVector());
  }

  @Override // from OfflineTableSupplier
  public final Tensor getTable() {
    return tableBuilder.toTable();
  }

  public final String channel() {
    return channel;
  }
}
