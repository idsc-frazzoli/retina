// code by mh
package ch.ethz.idsc.demo.mh;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.calib.steer.RimoTwdOdometry;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.VelocityHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
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
  private Scalar linmotpos = Quantity.of(0, SI.METER);
  private Scalar autonomyPressed = Quantity.of(0, SI.VOLT);
  private int count = 0;
  private int maxvount = 1000000;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(Vmu931ImuChannel.INSTANCE.channel())) {
      if (count < maxvount && Scalars.lessThan(Quantity.of(5, SI.VOLT), autonomyPressed)) {
        count++;
        Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
        // append to table
        if (gokartPoseEvent != null)
          tableBuilder.appendRow( //
              time.map(Magnitude.SECOND), //
              RealScalar.of(vmu931ImuFrame.timestamp_ms()), //
              VelocityHelper.toUnitless(gokartPoseEvent.getVelocity()).map(Round._5),
              SensorsConfig.GLOBAL.getPlanarVmu931Imu().accXY(vmu931ImuFrame).map(Magnitude.ACCELERATION).map(Round._5), //
              RealScalar.of(steerPosition.number().floatValue()), //
              powerPair.map(Magnitude.ARMS).map(Round._5), //
              powerAccelerationLeft.map(Magnitude.ACCELERATION).map(Round._5), //
              powerAccelerationRight.map(Magnitude.ACCELERATION).map(Round._5), //
              wheelSpeed.map(Magnitude.VELOCITY).map(Round._5), gokartPoseEvent.getPose().extract(0, 2).map(Magnitude.METER).map(Round._7),
              gokartPoseEvent.getPose().Get(2).map(Round._7), //
              linmotpos.map(Magnitude.METER).map(Round._7), //
              autonomyPressed.map(Magnitude.VOLT).map(Round._4));
      }
      // System.out.println("vmu time: "+time);
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      System.out.println(count + "pose time: " + time.number().doubleValue());
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
      wheelSpeed = RimoTwdOdometry.tangentSpeed(rge);
    } else if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      linmotpos = linmotGetEvent.getActualPosition();
    } else if (channel.equals(GokartLcmChannel.LABJACK_U3_ADC)) {
      LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(byteBuffer);
      autonomyPressed = labjackAdcFrame.getADC(3);
      /* JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
       * ManualControlInterface manualControlInterface = (ManualControlInterface) joystickEvent;
       * if(manualControlInterface.isAutonomousPressed()) {
       * autonomyPressed = RealScalar.ONE;
       * }else {
       * autonomyPressed = RealScalar.ZERO;
       * } */
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    System.out.println("building table");
    return tableBuilder.toTable();
  }
}
