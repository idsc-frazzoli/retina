// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.lidar.mark8.Mark8Device;
import ch.ethz.idsc.retina.lidar.mark8.Mark8Digest;

/** publishes the incoming TCP data from the Quanergy Marc8 via LCM
 * 
 * <p>if the sensor is the only client to the device, the sensor typically
 * requires 20 seconds to respond with the first measurements */
public class Mark8LcmServer {
  public static final int DEFAULT_RETURNS = 1;
  // ---
  private final String ip;
  private final Mark8Digest mark8Digest;
  private boolean isLaunched = true;
  private final BinaryBlobPublisher publisher;

  /** @param ip address for instance "192.168.1.3"
   * @param mark8Digest
   * @param lidarId for example "center", or "front" */
  public Mark8LcmServer(String ip, Mark8Digest mark8Digest, String lidarId) {
    this.ip = ip;
    this.mark8Digest = mark8Digest;
    publisher = new BinaryBlobPublisher(Mark8Device.channel(lidarId));
  }

  /** blocking call
   * 
   * @throws Exception */
  public void start() throws Exception {
    final byte[] data = new byte[Mark8Device.LENGTH];
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
          byte[] packet = mark8Digest.digest(data);
          publisher.accept(packet, packet.length);
        } else
          Thread.sleep(2);
    }
  }

  public void stop() {
    isLaunched = false;
  }
}
