// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lidar.VelodynePosEvent;
import ch.ethz.idsc.tensor.Scalar;

public class VelodynePosTable implements OfflineLogListener {
  private final String channel;
  private String nmea = "";
  private final List<String> list = new LinkedList<>();

  public VelodynePosTable(String channel) {
    this.channel = channel;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (this.channel.equals(channel)) {
      VelodynePosEvent velodynePosEvent = VelodynePosEvent.vlp16(byteBuffer);
      if (!nmea.equals(velodynePosEvent.nmea())) {
        nmea = velodynePosEvent.nmea();
        list.add(nmea);
      }
    }
  }

  public List<String> list() {
    return list;
  }
}
