// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.quanergy.mark8.Mark8Device;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

/** publishes the incoming tcp data from the Quanergy Marc8 via lcm
 * 
 * <p>if the sensor is the only client to the device, the sensor typically
 * requires 20 seconds to respond with the first measurements */
public class Mark8LcmServer {
  public static final int HEADER = 0x75bd7e97;
  public static final int LENGTH = 6632;
  // ---
  private final String ip;
  private boolean isLaunched = true;
  private final String channel;
  private final BinaryBlob binaryBlob = new BinaryBlob();

  /** @param ip for instance "192.168.1.3"
   * @param lidarId for example "top" */
  public Mark8LcmServer(String ip, String lidarId) {
    this.ip = ip;
    channel = "mark8." + lidarId + ".ray";
    binaryBlob.data_length = LENGTH;
    binaryBlob.data = new byte[LENGTH];
  }

  /** blocking call
   * 
   * @throws Exception */
  public void start() throws Exception {
    try (Socket socket = new Socket(ip, Mark8Device.TCP_PORT)) {
      InputStream inputStream = socket.getInputStream();
      while (isLaunched) {
        if (LENGTH <= inputStream.available()) {
          inputStream.read(binaryBlob.data);
          ByteBuffer message = ByteBuffer.wrap(binaryBlob.data);
          message.order(ByteOrder.BIG_ENDIAN);
          int header = message.getInt();
          int length = message.getInt();
          if (header != HEADER || length != LENGTH)
            throw new RuntimeException("data corruption");
          LCM.getSingleton().publish(channel, binaryBlob);
        } else {
          Thread.sleep(5);
        }
      }
    }
  }

  public void stop() {
    isLaunched = false;
  }

  public static void main(String[] args) throws Exception {
    Mark8LcmServer mark8LcmServer = new Mark8LcmServer("192.168.1.3", "center");
    mark8LcmServer.start();
  }
}
