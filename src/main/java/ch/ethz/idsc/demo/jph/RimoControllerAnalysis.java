// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.VectorFloatBlob;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;

class RimoControllerAnalysis implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final ByteOrder byteOrder;

  public RimoControllerAnalysis(ByteOrder byteOrder) {
    this.byteOrder = byteOrder;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.RIMO_CONTROLLER_PI)) {
      byteBuffer.order(byteOrder);
      Tensor tensor = VectorFloatBlob.decode(byteBuffer);
      tableBuilder.appendRow(time.map(Magnitude.SECOND), tensor);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    String string = "20180427T125356_p2";
    File file = UserHome.file("gokart/rimoctrl/" + string + "/log.lcm");
    OfflineTableSupplier offlineTableSupplier = new RimoControllerAnalysis(ByteOrder.BIG_ENDIAN);
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Export.of(UserHome.file(string + ".csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
  }
}
