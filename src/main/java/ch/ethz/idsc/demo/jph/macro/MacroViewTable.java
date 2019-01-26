// code by jph
package ch.ethz.idsc.demo.jph.macro;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.core.man.ManualControlParser;
import ch.ethz.idsc.gokart.dev.misc.MiscGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** export log files to tables for display in macro view image */
/* package */ class MacroViewTable implements OfflineTableSupplier {
  static final File ROOT = HomeDirectory.file("gokartproc");
  private static final int START_8AM = 480;
  static final int LENGTH = 660;
  private static final ScalarUnaryOperator MAX = Max.function(RealScalar.ONE);
  private static final ScalarUnaryOperator SEC2MIN = QuantityMagnitude.SI().in("min");
  private static final int INDEX_LOGE = 0;
  private static final int INDEX_RATE = 1;
  private static final int INDEX_AUTO = 2;
  private static final int INDEX_VOLT = 3;
  // ---
  private final Tensor table = Array.zeros(LENGTH, 4);
  private final Scalar offset;

  public MacroViewTable(Scalar offset) {
    this.offset = offset;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    int index = getMinute(time);
    if (0 <= index && index < LENGTH) {
      table.set(MAX, index, INDEX_LOGE);
      if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
        RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
        Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
        Scalar rate = Magnitude.VELOCITY.apply(speed).abs();
        table.set(Max.function(rate), index, INDEX_RATE);
      } else //
      if (channel.equals(MiscLcmServer.CHANNEL_GET)) {
        MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
        Scalar volt = Magnitude.VOLT.apply(miscGetEvent.getSteerBatteryVoltage());
        // TODO JPH it does not make sense to track the maximum steering voltage but rather the minimum
        table.set(Max.function(volt), index, INDEX_VOLT);
      } else //
      {
        Optional<ManualControlInterface> optional = ManualControlParser.event(channel, byteBuffer);
        if (optional.isPresent() && optional.get().isAutonomousPressed())
          table.set(MAX, index, INDEX_AUTO);
      }
    }
  }

  private int getMinute(Scalar time) {
    return SEC2MIN.apply(offset.add(time)).number().intValue() - START_8AM;
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return table;
  }

  public static void main(String[] args) throws Exception {
    ROOT.mkdir();
    for (LogFile logFile : DatahakiLogFileLocator.all()) {
      String date = logFile.getTitle().substring(0, 8);
      File dirday = new File(ROOT, date);
      dirday.mkdir();
      File file = DatahakiLogFileLocator.file(logFile);
      final File csv = new File(dirday, logFile.getTitle() + ".csv");
      if (csv.exists()) {
        Tensor table = Import.of(csv);
        if (table.length() < LENGTH) {
          table = Join.of(table, Array.zeros(LENGTH - table.length(), 4));
          Export.of(csv, table);
          System.out.println(csv + " " + Dimensions.of(table));
        }
      } else {
        System.out.println(logFile.getTitle());
        String hhmmss = logFile.getTitle().substring(9);
        Tensor timestamp = Tensors.fromString(String.format("{%s[h],%s[min],%s[s]}", //
            hhmmss.substring(0, 2), //
            hhmmss.substring(2, 4), //
            hhmmss.substring(4, 6))).map(Magnitude.SECOND);
        Scalar offset = Quantity.of(Total.of(timestamp).Get(), SI.SECOND);
        OfflineTableSupplier offlineTableSupplier = new MacroViewTable(offset);
        OfflineLogPlayer.process(file, offlineTableSupplier);
        Export.of(csv, offlineTableSupplier.getTable().map(CsvFormat.strict()));
      }
    }
  }
}
