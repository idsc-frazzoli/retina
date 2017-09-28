// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.io.UserHome;
import ch.ethz.idsc.tensor.StringScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayQ;
import ch.ethz.idsc.tensor.io.Export;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

enum GokartSteerLogExport {
  ;
  public static void main(String[] args) throws IOException {
    File file = new File("/media/datahaki/media/ethz/lcmlog", "20170926T165328_358c691c.lcm.00_sysid");
    Log log = new Log(file.toString(), "r");
    long countGet = 0;
    long countPut = 0;
    Tensor tableGet = Tensors.empty();
    Tensor tablePut = Tensors.empty();
    tableGet.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("motAsp_CANInput"), StringScalar.of("gcpRelRckPos")));
    tablePut.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("torque")));
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
          tablePut.append(Tensors.vector(event.utime - tic, steerPutEvent.getTorque()));
          ++countPut;
        }
      }
    } catch (Exception exception) {
      // ---
    }
    System.out.println("" + countGet);
    System.out.println("" + countPut);
    GlobalAssert.that(ArrayQ.of(tableGet));
    GlobalAssert.that(ArrayQ.of(tablePut));
    Export.of(UserHome.file("sysid_get.csv"), tableGet);
    Export.of(UserHome.file("sysid_put.csv"), tablePut);
  }
}
