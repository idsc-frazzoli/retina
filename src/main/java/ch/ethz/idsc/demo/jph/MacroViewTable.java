// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.function.UnaryOperator;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

class MacroViewTable implements OfflineTableSupplier {
  private static final String JOYSTICK = "joystick.generic_xbox_pad";
  private static final int START_8AM = 480;
  private static final int LENGTH = 600;
  private static final UnaryOperator<Scalar> MAX = Max.function(RealScalar.ONE);
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

  @Override
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
      if (channel.equals(JOYSTICK)) {
        JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
        GokartJoystickInterface gji = (GokartJoystickInterface) joystickEvent;
        if (gji.isAutonomousPressed())
          table.set(MAX, index, INDEX_AUTO);
      } else //
      if (channel.equals(MiscLcmServer.CHANNEL_GET)) {
        MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
        Scalar volt = Magnitude.VOLT.apply(miscGetEvent.getSteerBatteryVoltage());
        table.set(Max.function(volt), index, INDEX_VOLT);
      }
    }
  }

  private int getMinute(Scalar time) {
    return SEC2MIN.apply(offset.add(time)).number().intValue() - START_8AM;
  }

  @Override
  public Tensor getTable() {
    return table;
  }

  public static void main(String[] args) throws Exception {
    File root = UserHome.file("gokartproc");
    root.mkdir();
    final int start = -1; // GokartLogFile._20171213T161500_55710a6b.ordinal();
    for (GokartLogFile logFile : GokartLogFile.values())
      if (start <= logFile.ordinal()) {
        String date = logFile.getTitle().substring(0, 8);
        File dirday = new File(root, date);
        dirday.mkdir();
        File file = DatahakiLogFileLocator.file(logFile);
        if (file.isFile()) {
          File csv = new File(dirday, logFile.getTitle() + ".csv");
          if (!csv.exists()) {
            System.out.println(logFile.getTitle());
            String hhmmss = logFile.getTitle().substring(9);
            Tensor timestamp = Tensors.fromString(String.format("{%s[h],%s[min],%s[s]}", //
                hhmmss.substring(0, 2), //
                hhmmss.substring(2, 4), //
                hhmmss.substring(4, 6))).map(Magnitude.SECOND);
            Scalar offset = Quantity.of(Total.of(timestamp).Get(), "s");
            OfflineTableSupplier offlineTableSupplier = new MacroViewTable(offset);
            OfflineLogPlayer.process(file, offlineTableSupplier);
            Export.of( //
                csv, //
                offlineTableSupplier.getTable().map(CsvFormat.strict()));
          }
        }
      }
  }
}
