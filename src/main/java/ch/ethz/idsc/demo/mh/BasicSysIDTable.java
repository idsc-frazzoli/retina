// code by mh
package ch.ethz.idsc.demo.mh;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class BasicSysIDTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final SteerColumnTracker steerTracker = new SteerColumnTracker();
  private GokartPoseEvent gokartPoseEvent;
  private Scalar steerPosition = Quantity.of(0, "SCE");
  private Tensor powerPair = Tensors.vector(0, 0).multiply(Quantity.of(1, NonSI.ARMS));
  private Scalar wheelSpeed = Quantity.of(0, SI.VELOCITY);
  private Scalar powerAccelerationLeft = Quantity.of(0, SI.ACCELERATION);
  private Scalar powerAccelerationRight = Quantity.of(0, SI.ACCELERATION);

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(Vmu931ImuChannel.INSTANCE.channel())) {
      Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
      // append to table
      tableBuilder.appendRow( //
          time.map(Magnitude.SECOND).map(Round._6), //
          RealScalar.of(vmu931ImuFrame.timestamp_ms()), //
          gokartPoseEvent.getVelocityXY().map(Magnitude.VELOCITY).map(Round._5), //
          gokartPoseEvent.getGyroZ().map(Magnitude.PER_SECOND).map(Round._5), //
          SensorsConfig.getPlanarVmu931Imu().accXY(vmu931ImuFrame).map(Magnitude.ACCELERATION).map(Round._5), //
          RealScalar.of(steerPosition.number().floatValue()), //
          powerPair.map(Magnitude.ARMS).map(Round._5), //
          powerAccelerationLeft.map(Magnitude.ACCELERATION).map(Round._5), //
          powerAccelerationRight.map(Magnitude.ACCELERATION).map(Round._5), //
          wheelSpeed.map(Magnitude.VELOCITY).map(Round._5));
      // System.out.println("vmu time: "+time);
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      System.out.println("pose time: " + time.number().doubleValue());
    } else //
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent sge = new SteerGetEvent(byteBuffer);
      steerTracker.getEvent(sge);
      if (steerTracker.isCalibratedAndHealthy()) {
        steerPosition = steerTracker.getSteerColumnEncoderCentered();
        // System.out.println("steer time: "+time);
      }
    } else if (channel.equals(RimoLcmServer.CHANNEL_PUT)) {
      powerPair = RimoPutHelper.from(byteBuffer).getTorque_Y_pair();
      // System.out.println("power time: "+time);
      powerAccelerationLeft = PowerLookupTable.getInstance().getAcceleration(powerPair.Get(0), wheelSpeed);
      powerAccelerationRight = PowerLookupTable.getInstance().getAcceleration(powerPair.Get(1), wheelSpeed);
    } else if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rge = new RimoGetEvent(byteBuffer);
      wheelSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rge);
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
