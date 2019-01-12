// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.io.TableBuilder;

/** https://github.com/idsc-frazzoli/retina/files/1958519/20180428_steering_unit_range.pdf */
/* package */ class SteerRangeAnalysis implements OfflineLogListener {
  private final SteerColumnTracker steerColumnTracker = new SteerColumnTracker();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      steerColumnTracker.getEvent(steerGetEvent);
    }
  }

  public static void main(String[] args) throws IOException {
    int lo = GokartLogFile._20171213T161500_55710a6b.ordinal();
    int hi = GokartLogFile._20180112T154355_9e1d3699.ordinal();
    TableBuilder tableBuilder = new TableBuilder();
    for (GokartLogFile gokartLogFile : GokartLogFile.values())
      if (lo <= gokartLogFile.ordinal() && gokartLogFile.ordinal() <= hi) {
        SteerRangeAnalysis steerRangeAnalysis = new SteerRangeAnalysis();
        try {
          OfflineLogPlayer.process(DatahakiLogFileLocator.file(gokartLogFile), steerRangeAnalysis);
          Tensor range = Tensors.of( //
              StringScalar.of(gokartLogFile.name().substring(1)), //
              DoubleScalar.of(steerRangeAnalysis.steerColumnTracker.getIntervalWidth()));
          System.out.println(range);
          tableBuilder.appendRow(range);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    Export.of(HomeDirectory.file("steercolumntracker.csv"), tableBuilder.toTable().map(CsvFormat.strict()));
  }
}
