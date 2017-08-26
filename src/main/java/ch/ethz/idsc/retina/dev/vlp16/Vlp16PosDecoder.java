// code by jph
package ch.ethz.idsc.retina.dev.vlp16;

import java.nio.ByteBuffer;

/** information on p.17 of VLP-16 user's manual */
public class Vlp16PosDecoder extends ListenerQueue<Vlp16PosEventListener> {
  /** @param byteBuffer with at least 512 bytes to read */
  public void positioning(ByteBuffer byteBuffer) {
    final int offset = byteBuffer.position(); // 0 or 42 in pcap file
    byteBuffer.position(offset + 198); // unused
    Vlp16PosEvent vlp16PosEvent = new Vlp16PosEvent();
    vlp16PosEvent.gps_usec = byteBuffer.getInt(); // TODO from the hour?
    byteBuffer.getInt(); // unused
    byte[] nmea = new byte[72]; // NMEA positioning sentence
    byteBuffer.get(nmea);
    vlp16PosEvent.nmea = new String(nmea);
    // System.out.println(vlp16PosEvent.gps_usec + " " + vlp16PosEvent.nmea);
    listeners.forEach(listener -> listener.positioning(vlp16PosEvent));
  }
}
