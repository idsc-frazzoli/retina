// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

/** NMEA examples from VLP-16
 * $GPRMC,214431,A,3707.7937,N,12139.2432,W,000.0,325.8,230715,013.8,E,D*0F
 * $GPRMC,214432,A,3707.7939,N,12139.2428,W,001.4,175.2,230715,013.8,E,D*01 */
public class VelodynePosEvent {
  /** number of microseconds past the hour per UTC time */
  private final int gps_usec;
  /** The Validity field in the $GPRMC message (‘A’ or ‘V’) should be checked
   * by the user to ensure the GPS system and the VLP-16 are receiving valid
   * Coordinated Universal Time (UTC) updates from the user’s GPS receiver. */
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

  public void print() {
    System.out.println(gps_usec + " " + nmea);
  }
}
