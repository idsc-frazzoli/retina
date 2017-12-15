// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.util.gps.WGS84toCH1903LV03Plus;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.Export;
import lcm.logging.Log;
import lcm.logging.Log.Event;

enum GokartGpsLogExport {
  ;
  public static void main(String[] args) throws IOException {
    File file = new File("/home/datahaki", "20171213T164051_55710a6b.lcm.00");
    Log log = new Log(file.toString(), "r");
    final String channel = VelodyneLcmChannels.pos(VelodyneModel.VLP16, "center");
    Tensor trail = Tensors.empty();
    Tensor last = Tensors.vector(0, 0);
    Long tic = null;
    try {
      while (true) {
        Event event = log.readNext();
        if (tic == null)
          tic = event.utime;
        if (event.channel.equals(channel)) {
          // for (int c = 0; c < event.data.length - 10; ++c) {
          // if (event.data[c] == '$' && event.data[c + 1] == 'G' && event.data[c + 2] == 'P')
          // System.out.println(c);
          // }
          ByteBuffer byteBuffer = ByteBuffer.wrap(event.data); // length == 524
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          byteBuffer.position(12);
          VelodynePosEvent velodynePosEvent = VelodynePosEvent.vlp16(byteBuffer);
          Scalar degX = velodynePosEvent.gpsX();
          Scalar degY = velodynePosEvent.gpsY();
          Tensor metric = WGS84toCH1903LV03Plus.transform(degX, degY);
          if (!metric.equals(last)) {
            // System.out.println(metric);
            last = metric;
            Tensor append = Join.of(metric, Tensors.vector((event.utime - tic) * 1E-6));
            trail.append(append);
          }
          // System.out.println(x + " " + y);
          // System.out.println(velodynePosEvent.nmea());
        }
      }
    } catch (Exception exception) {
      // ---
    }
    Export.of(UserHome.file("gps_trail.csv"), trail);
  }
}
