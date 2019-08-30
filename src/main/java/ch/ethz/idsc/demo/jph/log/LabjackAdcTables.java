// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.LabjackAdcChannel;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class LabjackAdcTables implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(LabjackAdcChannel.INSTANCE.channel())) {
      LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(byteBuffer);
      tableBuilder.appendRow(labjackAdcFrame.asVector());
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.getTable();
  }

  public static void main(String[] args) {
    for (GokartLogFile gokartLogFile : GokartLogFile.values())
      if (GokartLogFile._20181213T133900_a04ee685.ordinal() <= gokartLogFile.ordinal()) {
        File export = HomeDirectory.Documents("maxjack", gokartLogFile.getTitle() + ".csv.gz");
        if (!export.exists())
          try {
            System.out.println(gokartLogFile);
            File file = DatahakiLogFileLocator.file(gokartLogFile);
            LabjackAdcTables gokartAdc = new LabjackAdcTables();
            OfflineLogPlayer.process(file, gokartAdc);
            Tensor tensor = gokartAdc.getTable();
            Export.of(export, tensor);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
      }
  }
}
