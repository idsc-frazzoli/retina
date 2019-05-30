// code by jph
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
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    //String string = "20180427T125356_p2";
    File file = HomeDirectory.file("Documents/01_6_Semester/Bachelorarbeit/20190523/20190523T163610_6a3866ff.lcm.00");
    OfflineTableSupplier offlineTableSupplier = new AntilockAnalysis();
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Export.of(HomeDirectory.file("Documents/01_6_Semester/Bachelorarbeit/cuts1/ffdss.csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
  }
}
