// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/** file description taken from "Hani's blog: A look at the pcap file format"
 * http://www.kroosec.com/2012/10/a-look-at-pcap-file-format.html
 * 
 * implementation is standalone */
public class PcapParse {
  private static final int HEADER_ID = 0xa1b2c3d4;

  /** @param file
   * @param pcapPacketListeners
   * @throws Exception */
  public static void of(File file, PcapPacketListener... pcapPacketListeners) throws IOException {
    new PcapParse(file, Arrays.asList(pcapPacketListeners));
  }
  // ---

  private final InputStream inputStream;
  private final byte[] packet_header = new byte[16];
  private int max_size;
  private byte[] packet_data;

  private PcapParse(File file, List<PcapPacketListener> pcapPacketListeners) throws IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      this.inputStream = inputStream;
      _globalHeader();
      // ---
      while (0 < inputStream.available()) {
        inputStream.read(packet_header); // packet header
        ByteBuffer byteBuffer = ByteBuffer.wrap(packet_header);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        int sec = byteBuffer.getInt(); // sec
        int usec = byteBuffer.getInt(); // microseconds [0...999999]
        // The third field is 4 bytes long and contains the size of the saved packet
        // data in our file in bytes.
        int length = byteBuffer.getInt(); // size
        if (max_size < length)
          System.err.println(length + " " + max_size);
        _assert(length <= max_size);
        // The Fourth field is 4 bytes long too and contains the length of the packet as
        // it was captured on the wire.
        int length_data = byteBuffer.getInt(); // size
        _assert(length_data <= length);
        _assert(length_data <= max_size);
        _assert(byteBuffer.position() == 16);
        // packet data
        int number = inputStream.read(packet_data, 0, length);
        _assert(number == length);
        pcapPacketListeners.forEach(listener -> listener.pcapPacket(sec, usec, packet_data, length_data));
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
    _assert(fid == HEADER_ID);
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
