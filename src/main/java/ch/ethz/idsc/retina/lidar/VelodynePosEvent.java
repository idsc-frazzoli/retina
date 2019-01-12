// code by jph
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** NMEA examples from VLP-16
 * $GPRMC,214431,A,3707.7937,N,12139.2432,W,000.0,325.8,230715,013.8,E,D*0F
 * $GPRMC,214432,A,3707.7939,N,12139.2428,W,001.4,175.2,230715,013.8,E,D*01
 * 
 * at Duebendorf
 * $GPRMC,155524,A,4724.3266,N,00837.8624,E,002.0,172.1,131217,001.8,E,A*10
 * $GPRMC,142802,A,4724.3445,N,00837.8776,E,000.0,111.4,080118,001.8,E,A*1B
 * 
 * Example of invalid
 * FIXME parse with string tokenizer or string#split if necessary
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

  /** The validity field in the $GPRMC message (‘A’ or ‘V’) should be checked by
   * the user to ensure the GPS system and the VLP-16 are receiving valid
   * Coordinated Universal Time (UTC) updates from the user’s GPS receiver.
   * validity: A=ok, V=invalid
   * 
   * @return */
  public boolean isValid() {
    return nmea.charAt(14) == 'A';
  }

  public String timeStamp() {
    return nmea.substring(7, 13);
  }

  public String dateStamp() {
    return nmea.substring(53, 59);
  }

  public Scalar speed() {
    double value = Double.parseDouble(nmea.substring(41, 46));
    return Quantity.of(value, "knots");
  }

  public Scalar course() {
    double value = Double.parseDouble(nmea.substring(47, 52));
    return Quantity.of(value, NonSI.DEGREE_ANGLE);
  }

  private static final double TO_DEGREE_ANGLE = 1E-2;

  /** E W
   * 
   * @return */
  public Scalar gpsX() {
    double value = Double.parseDouble(nmea.substring(28, 28 + 10)) * TO_DEGREE_ANGLE;
    Scalar scalar = Quantity.of(value, NonSI.DEGREE_ANGLE);
    char id = nmea.charAt(39);
    return id == 'E' ? scalar : scalar.negate();
  }

  /** N S
   * 
   * @return */
  public Scalar gpsY() {
    double value = Double.parseDouble(nmea.substring(16, 16 + 9)) * TO_DEGREE_ANGLE;
    Scalar scalar = Quantity.of(value, NonSI.DEGREE_ANGLE);
    char id = nmea.charAt(25 + 1);
    return id == 'N' ? scalar : scalar.negate();
  }
}
