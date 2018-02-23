// code by jph
package ch.ethz.idsc.demo.ni;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.dev.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayQ;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.StringScalar;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

// TODO make this a function
enum GokartGpsPoseExport {
  ;
  public static void main(String[] args) throws IOException {
    File file = new File("C:\\Users\\maste_000\\Documents\\ETH\\LogFilesKart\\1218", "20171218T121006_9b56b71b.lcm.00.extract");
    file = new File("/home/datahaki", "20180108T162528_5f742add.lcm.00.extract");
    Log log = new Log(file.toString(), "r");
    long countGet = 0;
    long countPut = 0;
    Tensor tableGet = Tensors.empty();
    Tensor tablePut = Tensors.empty();
    tableGet.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("PosX"), StringScalar.of("PosY")));
    tablePut.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("GpsX"), StringScalar.of("GpsY")));
    Long tic = null;
    try {
      while (true) {
        Event event = log.readNext();
        if (Objects.isNull(tic))
          tic = event.utime;
        if (event.channel.equals("gokart.pose.lidar")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(byteBuffer);
          tableGet.append(Tensors.vector(event.utime - tic, gokartPoseEvent.getPose().Get(0).number(), gokartPoseEvent.getPose().Get(1).number()));
          ++countGet;
        } else //
        if (event.channel.equals("vlp16.center.pos")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          VelodynePosEvent velodynePosEvent = VelodynePosEvent.vlp16(byteBuffer);
          Tensor coord = WGS84toCH1903LV03Plus.transform(velodynePosEvent.gpsX(), velodynePosEvent.gpsY());
          tablePut.append(Tensors.vector(event.utime - tic, coord.Get(0).number(), coord.Get(1).number()));
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
    File dir = UserHome.file("GokartPosGps");
    dir.mkdir();
    Export.of(new File(dir, "Pos.csv"), tableGet);
    Export.of(new File(dir, "Gps.csv"), tablePut);
  }
}
