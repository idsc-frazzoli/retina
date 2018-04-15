// code by jph and az
package ch.ethz.idsc.demo.az;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.fuse.ComputerSensorsEvent;
import ch.ethz.idsc.gokart.core.fuse.ComputerSensorsModule;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;

class TemperatureEval implements OfflineTableSupplier {
  TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(ComputerSensorsModule.CHANNEL_GET)) {
      System.out.println("here"); // TODO
      ComputerSensorsEvent cse = new ComputerSensorsEvent(byteBuffer);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND), //
          cse.getTemperatureMax().map(Magnitude.DEGREE_CELSIUS) //
      );
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    TemperatureEval temperatureEval = new TemperatureEval();
    OfflineLogPlayer.process(new File("some.lcm.00"), temperatureEval); // TODO filename
    Export.of(UserHome.file("computersensors.csv"), temperatureEval.getTable().map(CsvFormat.strict()));
  }
}
