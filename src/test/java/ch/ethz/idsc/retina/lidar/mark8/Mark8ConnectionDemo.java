// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** the quanergy server buffers up to 300 kB of data */
enum Mark8ConnectionDemo {
  ;
  @SuppressWarnings("unused")
  public static void main(String[] args) throws Exception {
    try (Socket socket = new Socket("192.168.1.3", Mark8Device.TCP_PORT)) {
      InputStream inputStream = socket.getInputStream();
      byte[] data = new byte[32768];
      byte[] msg = new byte[Mark8Device.LENGTH];
      while (true) {
        if (Mark8Device.LENGTH <= inputStream.available()) {
          inputStream.read(msg);
          ByteBuffer message = ByteBuffer.wrap(msg);
          message.order(ByteOrder.BIG_ENDIAN);
          int header = message.getInt();
          if (header != Mark8Device.HEADER)
            throw new RuntimeException();
          int length = message.getInt();
          if (length != Mark8Device.LENGTH)
            throw new RuntimeException();
          // ---
          int timestamp_seconds = message.getInt();
          int timestamp_nanos = message.getInt();
          byte api_version_major = message.get();
          byte api_version_minor = message.get();
          byte api_version_patch = message.get();
          byte packet_type = message.get();
          // our quanergy only sends packet-type 0x00
          System.out.println("packet_type=" + packet_type);
          // READ FIRING DATA [50]
          for (int index = 0; index < 50; ++index) {
            /** rotation [0, ..., 10399] */
            int position = message.getShort();
            short reserved = message.getShort();
            for (int count = 0; count < 24; ++count)
              message.getInt(); // distances
            for (int count = 0; count < 24; ++count)
              message.get(); // intensities
            for (int count = 0; count < 8; ++count)
              message.get(); // status
          }
          message.getInt(); // timestamp seconds
          message.getInt(); // timestamp nanos
          message.getShort(); // API version
          message.getShort(); // status
          System.out.println("remaining=" + message.remaining());
        } else {
          Thread.sleep(5);
        }
      }
    }
  }
}
