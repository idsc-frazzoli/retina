// code by jph
// TODO cite web reference
package ch.ethz.idsc.retina.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** information taken from
 * "Hani's blog: A look at the pcap file format" */
public class PcapParse {
  final InputStream inputStream;
  final byte[] packet_header = new byte[16];
  int max_size;
  byte[] packet_data;

  public PcapParse(File file, PacketConsumer packetConsumer) throws Exception {
    try (InputStream inputStream = new FileInputStream(file)) {
      this.inputStream = inputStream;
      _globalHeader();
      // ---
      while (0 < inputStream.available()) {
        inputStream.read(packet_header); // packet header
        ByteBuffer byteBuffer = ByteBuffer.wrap(packet_header);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.getInt(); // sec
        byteBuffer.getInt(); // msec
        // The third field is 4 bytes long and contains the size of the saved packet data in our file in bytes.
        int length = byteBuffer.getInt(); // size
        // The Fourth field is 4 bytes long too and contains the length of the packet as it was captured on the wire.
        int length_data = byteBuffer.getInt(); // size
        if (length < length_data)
          System.err.println(length + " " + length_data + " " + max_size);
        _assert(length_data <= length);
        _assert(length_data <= max_size);
        _assert(byteBuffer.position() == 16);
        // packet data
        int number = inputStream.read(packet_data, 0, length);
        _assert(number == length);
        packetConsumer.parse(packet_data, length_data);
      }
    }
  }

  private void _globalHeader() throws IOException {
    byte[] bytes = new byte[24];
    inputStream.read(bytes);
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    int fid = byteBuffer.getInt();
    // System.out.println(String.format("%08x",fid));
    _assert(fid == 0xa1b2c3d4);
    // version
    int ver_major = byteBuffer.getShort();
    _assert(ver_major == 2);
    int ver_minor = byteBuffer.getShort();
    _assert(ver_minor == 4);
    byteBuffer.getInt(); // time related
    byteBuffer.getInt(); // time related
    max_size = byteBuffer.getInt(); // packet max size, typically 65535
    packet_data = new byte[max_size];
    // System.out.println(max_size);
    int link_layer = byteBuffer.getInt(); // Link-Layer Header Type
    _assert(link_layer == 1); // Ethernet
    _assert(byteBuffer.position() == 24);
  }

  private static void _assert(boolean check) {
    if (!check)
      throw new RuntimeException();
  }
}
