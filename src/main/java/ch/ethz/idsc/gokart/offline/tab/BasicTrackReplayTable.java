// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Round;

/** class produces table with the following columns:
 * 
 * time [s]
 * rimo torque left [ARMS]
 * rimo torque right [ARMS]
 * rimo rate left [rad*s^-1]
 * rimo rate right [rad*s^-1]
 * tangent speed [m*s^-1]
 * rotational rate [rad*s^-1]
 * gyro rate around gokart z-axis [rad*s^-1]
 * steering column encoder [SCE]
 * brake position [m]
 * localization pose x [m]
 * localization pose y [m]
 * localization pose theta [rad]
 * localization pose quality */
public class BasicTrackReplayTable implements OfflineTableSupplier {
  private static final String CHANNEL_IMU = //
      DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  // ---
  private DavisImuFrame davisImuFrame;
  private RimoGetEvent rimoGetEvent;
  private RimoPutEvent rimoPutEvent;
  private LinmotGetEvent linmotGetEvent;
  private GokartStatusEvent gokartStatusEvent;
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL_IMU)) {
      davisImuFrame = new DavisImuFrame(byteBuffer);
    } else //
    if (channel.equals(LinmotLcmServer.CHANNEL_GET))
      linmotGetEvent = new LinmotGetEvent(byteBuffer);
    else //
    if (channel.equals(RimoLcmServer.CHANNEL_GET))
      rimoGetEvent = new RimoGetEvent(byteBuffer);
    else //
    if (channel.equals(RimoLcmServer.CHANNEL_PUT))
      rimoPutEvent = RimoPutHelper.from(byteBuffer);
    else //
    if (channel.equals(GokartLcmChannel.STATUS))
      gokartStatusEvent = new GokartStatusEvent(byteBuffer);
    else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      if (Objects.isNull(davisImuFrame) || //
          Objects.isNull(linmotGetEvent) || //
          Objects.isNull(rimoGetEvent) || //
          Objects.isNull(rimoPutEvent) || //
          Objects.isNull(gokartStatusEvent))
        return;
      GokartPoseEvent gpe = new GokartPoseEvent(byteBuffer);
      Tensor xya = GokartPoseHelper.toUnitless(gpe.getPose());
      Tensor rates = rimoGetEvent.getAngularRate_Y_pair();
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rimoGetEvent);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          rimoPutEvent.getTorque_Y_pair().map(Magnitude.ARMS), //
          rates.map(Magnitude.PER_SECOND), //
          speed.map(Magnitude.VELOCITY), //
          rate.map(Magnitude.PER_SECOND), //
          davisImuFrame.gyroImageFrame().Get(1).map(Magnitude.PER_SECOND), //
          SteerPutEvent.ENCODER.apply(gokartStatusEvent.getSteerColumnEncoderCentered()), //
          linmotGetEvent.getActualPosition().map(Magnitude.METER).map(Round._6), //
          gpe.asVector().map(Round._6), //
          xya.extract(0, 2).map(Round._3), //
          xya.Get(2).map(Round._6), //
          gpe.getQuality() //
      );
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
