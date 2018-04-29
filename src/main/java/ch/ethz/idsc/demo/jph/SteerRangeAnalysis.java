// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.DubendorfHangarLog;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.io.TableBuilder;

class SteerRangeAnalysis implements OfflineLogListener {
  private final SteerColumnTracker steerColumnTracker = new SteerColumnTracker();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      steerColumnTracker.getEvent(steerGetEvent);
    }
  }

  private static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    // int lo = DubendorfHangarLog._20180226T150533_ed1c7f0a.ordinal();
    // int hi = DubendorfHangarLog._20180427T155709_987cb124.ordinal();
    int lo = DubendorfHangarLog._20171213T161500_55710a6b.ordinal();
    int hi = DubendorfHangarLog._20180112T154355_9e1d3699.ordinal();
    TableBuilder tableBuilder = new TableBuilder();
    for (DubendorfHangarLog dubendorfHangarLog : DubendorfHangarLog.values())
      if (lo <= dubendorfHangarLog.ordinal() && dubendorfHangarLog.ordinal() <= hi) {
        SteerRangeAnalysis steerRangeAnalysis = new SteerRangeAnalysis();
        try {
          OfflineLogPlayer.process(dubendorfHangarLog.file(LOG_ROOT), steerRangeAnalysis);
          Tensor range = Tensors.of( //
              StringScalar.of(dubendorfHangarLog.title()), //
              DoubleScalar.of(steerRangeAnalysis.steerColumnTracker.getIntervalWidth()));
          System.out.println(range);
          tableBuilder.appendRow(range);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    Export.of(UserHome.file("steercolumntracker.csv"), tableBuilder.toTable().map(CsvFormat.strict()));
  }
}
