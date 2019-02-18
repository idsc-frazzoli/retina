// code by az
package ch.ethz.idsc.demo.az;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class RimoControllerAnalysis implements OfflineLogListener {
  private final TableBuilder tableBuilder1 = new TableBuilder();
  private final TableBuilder tableBuilder2 = new TableBuilder();
  private final ByteOrder byteOrder;

  public RimoControllerAnalysis(ByteOrder byteOrder) {
    this.byteOrder = byteOrder;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.RIMO_CONTROLLER_AW)) {
      byteBuffer.order(byteOrder);
      Tensor tensor = VectorFloatBlob.decode(byteBuffer);
      tableBuilder1.appendRow( //
          time.map(Magnitude.SECOND), //
          tensor);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rge = new RimoGetEvent(byteBuffer);
      tableBuilder2.appendRow( //
          time.map(Magnitude.SECOND), //
          rge.getAngularRate_Y_pair().map(Magnitude.PER_SECOND));
    }
  }

  public Tensor getTable1() { // pi
    return tableBuilder1.toTable();
  }

  public Tensor getTable2() { // rimo
    return tableBuilder2.toTable();
  }

  public static void main(String[] args) throws IOException {
    String string = "20180830T151854_21b2e8ae";
    File file = HomeDirectory.file("datasets/gokartlogs/" + string + ".lcm.00");
    RimoControllerAnalysis offlineTableSupplier = new RimoControllerAnalysis(ByteOrder.LITTLE_ENDIAN);
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Export.of(HomeDirectory.file("git_cloned/car_model/MATLAB/gokartSYSID/rimo_pi/" + string + "_pi" + ".csv"),
        offlineTableSupplier.getTable1().map(CsvFormat.strict()));
    Export.of(HomeDirectory.file("git_cloned/car_model/MATLAB/gokartSYSID/rimo_pi/" + string + "_rimo" + ".csv"),
        offlineTableSupplier.getTable2().map(CsvFormat.strict()));
  }
}
