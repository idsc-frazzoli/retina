// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class GyroBiasAnalysis implements OfflineTableSupplier {
  private static final String CHANNEL_DAVIS_IMU = //
      DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final int LIMIT = 60 * 1000;
  // ---
  int count = 0;
  TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (count < LIMIT) {
      if (CHANNEL_DAVIS_IMU.equals(channel)) {
        DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
        Scalar scalar = davisImuFrame.gyroImageFrame().Get(1);
        tableBuilder.appendRow(scalar.map(Magnitude.PER_SECOND));
        ++count;
      }
    } else
      throw new RuntimeException("EOF");
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    for (GokartLogFile gokartLogFile : GokartLogFile.values())
      if (GokartLogFile._20181206T110202_3309d8c4.ordinal() <= gokartLogFile.ordinal()) {
        // System.out.println(gokartLogFile);
        File file = DatahakiLogFileLocator.file(gokartLogFile);
        // System.out.println(file);
        GyroBiasAnalysis gyroBiasAnalysis = new GyroBiasAnalysis();
        OfflineLogPlayer.process(file, gyroBiasAnalysis);
        Tensor tensor = gyroBiasAnalysis.getTable();
        Export.of(new File( //
            "/media/datahaki/media/ethz/gokartexport/gyrobias", //
            gokartLogFile.getTitle() + ".csv"), tensor.map(CsvFormat.strict()));
      }
  }
}
