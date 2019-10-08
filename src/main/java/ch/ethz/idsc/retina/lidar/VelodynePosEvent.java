// code by jph
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.gps.Gprmc;

/** NMEA examples from VLP-16
 * $GPRMC,214431,A,3707.7937,N,12139.2432,W,000.0,325.8,230715,013.8,E,D*0F
 * $GPRMC,214432,A,3707.7939,N,12139.2428,W,001.4,175.2,230715,013.8,E,D*01
 * 
 * at Duebendorf
 * $GPRMC,155524,A,4724.3266,N,00837.8624,E,002.0,172.1,131217,001.8,E,A*10
 * $GPRMC,142802,A,4724.3445,N,00837.8776,E,000.0,111.4,080118,001.8,E,A*1B
 * $GPRMC,150038,A,4724.3422,N,00837.9060,E,000.0,052.3,260519,001.8,E,D*14
 * 
 * Example of invalid
 * $GPRMC,145817,V,4724.3230,N,00837.8329,E,,,120118,001.8,E,N*04
 * 
 * in VLP-16 lcm package the $GPRMC is at byte offset 218 */
public class VelodynePosEvent {
  /** @param byteBuffer
   * @return */
  public static VelodynePosEvent vlp16(ByteBuffer byteBuffer) {
    final int offset = byteBuffer.position(); // 0 or 42 in pcap file
    byteBuffer.position(offset + 198); // unused
    int gps_usec = byteBuffer.getInt(); // TODO from the hour?
    byteBuffer.getInt(); // unused
    byte[] nmea = new byte[72]; // NMEA positioning sentence
    byteBuffer.get(nmea);
    return new VelodynePosEvent(gps_usec, new String(nmea));
  }

  // ---
  /** number of microseconds past the hour per UTC time */
  private final int gps_usec;
  private final String nmea;

  public VelodynePosEvent(int gps_usec, String nmea) {
    this.gps_usec = gps_usec;
    this.nmea = nmea;
  }

  public int gps_usec() {
    return gps_usec;
  }

  public String nmea() {
    return nmea;
  }

  /** @return
   * @throws Exception in rare cases when the nmea string from velodyne
   * does not start with "$GPRMC" and is not a valid positioning string */
  public Gprmc gprmc() {
    return Gprmc.of(nmea);
  }
}
