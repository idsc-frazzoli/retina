// code by am, jph
package ch.ethz.idsc.demo.am;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class AntilockAnalysis implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.LINMOT_ANTILOCK)) {
      Tensor tensor = VectorFloatBlob.decode(byteBuffer);
      tableBuilder.appendRow(time.map(Magnitude.SECOND), tensor);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.getTable();
  }

  public static void main(String[] args) throws IOException {
    // String string = "20180427T125356_p2";
    File file = HomeDirectory.file("/gokartlogs/20190729/20190729T170004_e1fcde97.lcm.00");
    OfflineTableSupplier offlineTableSupplier = new AntilockAnalysis();
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Export.of(HomeDirectory.file("Desktop", "lanekeeping1701.csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
  }
}
