// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Device;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

/** publishes the incoming tcp data from the Quanergy Marc8 via lcm
 * 
 * <p>if the sensor is the only client to the device, the sensor typically
 * requires 20 seconds to respond with the first measurements */
public class Mark8LcmServer {
  private final String ip;
  private boolean isLaunched = true;
  private final BinaryBlobPublisher publisher;

  /** @param ip for instance "192.168.1.3"
   * @param lidarId for example "top" */
  public Mark8LcmServer(String ip, String lidarId) {
    this.ip = ip;
    publisher = new BinaryBlobPublisher(Mark8Device.channel(lidarId));
  }

  /** blocking call
   * 
   * @throws Exception */
  public void start() throws Exception {
    int data_length = Mark8Device.LENGTH;
    byte[] data = new byte[Mark8Device.LENGTH];
    try (Socket socket = new Socket(ip, Mark8Device.TCP_PORT)) {
      InputStream inputStream = socket.getInputStream();
      while (isLaunched)
        if (Mark8Device.LENGTH <= inputStream.available()) {
          int read = inputStream.read(data);
          if (read != Mark8Device.LENGTH)
            throw new RuntimeException();
          {
            ByteBuffer message = ByteBuffer.wrap(data);
            message.order(ByteOrder.BIG_ENDIAN);
            int header = message.getInt();
            int length = message.getInt();
            if (header != Mark8Device.HEADER || length != Mark8Device.LENGTH)
              throw new RuntimeException("data corruption");
          }
          publisher.accept(data, data_length);
        } else
          Thread.sleep(2);
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
