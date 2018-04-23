// code by edo
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.ControllerInfoPublish;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayQ;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.StringScalar;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

/** code helped to perform system identification of the steering mechanism */
enum GokartSteerLogExport {
  ;
  public static void main(String[] args) throws IOException {
    File file = UserHome.file("20171120T210940_46f687f6.lcm.00"); // put hte filename here
    Log log = new Log(file.toString(), "r");
    long countGet = 0;
    long countPut = 0;
    Tensor tableGet = Tensors.empty();
    Tensor tablePut = Tensors.empty();
    Tensor tablePutRef = Tensors.empty();
    tableGet.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("motAsp_CANInput"), StringScalar.of("gcpRelRckPos")));
    tablePut.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("torque")));
    tablePutRef.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("positionRef"), StringScalar.of("position")));
    Long tic = null;
    try {
      while (true) {
        Event event = log.readNext();
        if (Objects.isNull(tic))
          tic = event.utime;
        if (event.channel.equals("autobox.steer.get")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
          tableGet.append(Tensors.vector(event.utime - tic, steerGetEvent.motAsp_CANInput, steerGetEvent.getGcpRelRckPos()));
          // System.out.println(steerGetEvent.getSteeringAngle());
          ++countGet;
        } else //
        if (event.channel.equals("autobox.steer.put")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          SteerPutEvent steerPutEvent = SteerPutEvent.from(byteBuffer);
          tablePut.append(Tensors.vector(event.utime - tic, steerPutEvent.getTorque().number().floatValue()));
          ++countPut;
        } else //
        if (event.channel.equals(ControllerInfoPublish.CHANNEL)) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          double value = byteBuffer.getDouble();
          double value2 = byteBuffer.getDouble();
          tablePutRef.append(Tensors.vector(event.utime - tic, value, value2));
        }
      }
    } catch (Exception exception) {
      // ---
    }
    System.out.println("" + countGet);
    System.out.println("" + countPut);
    GlobalAssert.that(ArrayQ.of(tableGet));
    GlobalAssert.that(ArrayQ.of(tablePut));
    GlobalAssert.that(ArrayQ.of(tablePutRef));
    Export.of(UserHome.file("sysid_get.csv"), tableGet);
    Export.of(UserHome.file("sysid_put.csv"), tablePut);
    Export.of(UserHome.file("sysid_putRef.csv"), tablePutRef);
  }
}
