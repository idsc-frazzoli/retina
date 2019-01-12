// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class LabjackU3Analysis implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (GokartLcmChannel.LABJACK_U3_ADC.equals(channel)) {
      LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(byteBuffer);
      tableBuilder.appendRow( //
          Magnitude.SECOND.apply(time), //
          labjackAdcFrame.asVector());
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    LabjackU3Analysis labjackU3Analysis = new LabjackU3Analysis();
    File file = DatahakiLogFileLocator.file(GokartLogFile._20181213T154338_6728a721);
    OfflineLogPlayer.process(file, labjackU3Analysis);
    Tensor tensor = labjackU3Analysis.getTable();
    Export.of(HomeDirectory.file("labjackadc.csv"), tensor.map(CsvFormat.strict()));
  }
}
