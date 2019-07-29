// code by mcp
package ch.ethz.idsc.demo.mp;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.VelocityHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/** export for UKF/EKF used in Parameter identification in Pacejka */
/* package */ class PacejkaAnalysis implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private GokartPoseEvent gokartPoseEvent;
  private Scalar steerPosition = Quantity.of(0, "SCE");
  private Scalar wheelAngularSpeedL = Quantity.of(0, SI.PER_SECOND);
  private Scalar wheelAngularSpeedR = Quantity.of(0, SI.PER_SECOND);
  private Scalar slipBackL = Quantity.of(0, SI.ONE);
  private Scalar slipBackR = Quantity.of(0, SI.ONE);
  /** https://www.dunlop.eu/dunlop_dede/Images/Dunlop-Kartreifen-Groessentabelle-2014-2016_tcm430-96714.pdf */
  private Scalar wheelRadiusBack = Quantity.of(0.120, SI.METER);
  private int count = 0;
  private int countMax = 50000;
  private int startTime = 150;
  private int endTime = 200;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(Vmu931ImuChannel.INSTANCE.channel()) && count < countMax) {
      Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
      if (Objects.nonNull(gokartPoseEvent)) {
        Scalar velX = gokartPoseEvent.getVelocity().Get(0);
        if (velX.number().doubleValue() > 0.10) {
          if (time.number().doubleValue() > startTime && time.number().doubleValue() < endTime) {
            Scalar wheelVelL = wheelRadiusBack.multiply(wheelAngularSpeedL);
            Scalar wheelVelR = wheelRadiusBack.multiply(wheelAngularSpeedR);
            slipBackL = wheelVelL.divide(velX).subtract(RealScalar.ONE);
            slipBackR = wheelVelR.divide(velX).subtract(RealScalar.ONE);
            tableBuilder.appendRow( //
                time.map(Magnitude.SECOND), // [1]
                RealScalar.of(vmu931ImuFrame.timestamp_ms()), // [2]
                VelocityHelper.toUnitless(gokartPoseEvent.getVelocity()).map(Round._5), // [3][4][5]
                SensorsConfig.GLOBAL.getPlanarVmu931Imu().acceleration(vmu931ImuFrame) //
                    .map(Magnitude.ACCELERATION).map(Round._5), // [6][7][8]
                wheelAngularSpeedL.map(Magnitude.PER_SECOND), // [9]
                wheelAngularSpeedR.map(Magnitude.PER_SECOND), // [10]
                RealScalar.of(steerPosition.number().floatValue()), // [11] //in radiants
                slipBackL.map(Round._5), // [12]
                slipBackR.map(Round._5) // [13]
            );
            count++;
          }
        }
      }
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rge = new RimoGetEvent(byteBuffer);
      wheelAngularSpeedL = rge.getTireL.getAngularRate_Y();
      wheelAngularSpeedR = rge.getTireR.getAngularRate_Y();
    } else //
    if (channel.equals(GokartLcmChannel.STATUS)) {
      GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(byteBuffer);
      if (gokartStatusEvent.isSteerColumnCalibrated())
        steerPosition = gokartStatusEvent.getSteerColumnEncoderCentered();
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    // String fileName = String.valueOf("20190627T133639_12dcbfa8.lcm.00");
    String fileName = String.valueOf("20190708T114135_f3f46a8b.lcm.00");
    File file = HomeDirectory.Downloads(fileName);
    PacejkaAnalysis pacejkaAnalysis = new PacejkaAnalysis();
    OfflineLogPlayer.process(file, //
        pacejkaAnalysis);
    Export.of(HomeDirectory.Documents("sp/logs/pacejkaFull_" + fileName + ".csv"), //
        pacejkaAnalysis.getTable().map(CsvFormat.strict()));
    System.out.println("process ended");
  }
}
