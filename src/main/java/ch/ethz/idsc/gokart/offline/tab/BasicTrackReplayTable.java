// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.steer.GokartStatusEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
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
 * 0
 * steering column encoder [SCE]
 * brake position [m]
 * localization pose x [m]
 * localization pose y [m]
 * localization pose theta [rad]
 * localization pose quality
 * localization velocity ux [m*s^-1]
 * localization velocity uy [m*s^-1]
 * localization rate omega [s^-1] */
public class BasicTrackReplayTable implements OfflineTableSupplier {
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  private RimoPutEvent rimoPutEvent = RimoPutEvent.PASSIVE;
  private LinmotGetEvent linmotGetEvent = LinmotGetEvents.ZEROS;
  private GokartStatusEvent gokartStatusEvent = GokartStatusEvents.UNKNOWN;
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
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
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      Tensor rates = rimoGetEvent.getAngularRate_Y_pair();
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rimoGetEvent);
      Scalar sce = gokartStatusEvent.isSteerColumnCalibrated() //
          ? gokartStatusEvent.getSteerColumnEncoderCentered()
          : Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          rimoPutEvent.getTorque_Y_pair().map(Magnitude.ARMS), //
          rates.map(Magnitude.PER_SECOND), //
          speed.map(Magnitude.VELOCITY), //
          rate.map(Magnitude.PER_SECOND), //
          RealScalar.ZERO, //
          SteerPutEvent.ENCODER.apply(sce), //
          linmotGetEvent.getActualPosition().map(Magnitude.METER).map(Round._6), //
          gokartPoseEvent.asVector() //
      );
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
