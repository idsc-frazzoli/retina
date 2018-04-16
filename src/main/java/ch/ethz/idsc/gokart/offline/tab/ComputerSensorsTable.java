// code by jph and az
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.gokart.core.fuse.ComputerSensorsEvent;
import ch.ethz.idsc.gokart.core.fuse.ComputerSensorsModule;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class ComputerSensorsTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Set<String> set = new HashSet<>();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (set.add(channel))
      System.out.println(channel);
    if (channel.equals(ComputerSensorsModule.CHANNEL_GET)) {
      System.out.println("here");
      ComputerSensorsEvent computerSensorsEvent = new ComputerSensorsEvent(byteBuffer);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND), //
          computerSensorsEvent.getTemperatureMax().map(Magnitude.DEGREE_CELSIUS), //
          computerSensorsEvent.getTemperatureMin().map(Magnitude.DEGREE_CELSIUS) //
      );
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
