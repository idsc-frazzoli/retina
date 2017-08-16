// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

/** NMEA examples
 * $GPRMC,131653,A,4722.6848,N,00832.8727,E,000.1,276.2,200717,001.8,E,D*15
 * $GPRMC,220642,A,3707.8228,N,12139.2776,W,013.7,145.4,111212,013.8,E,D*08 */
public class Hdl32ePositioningEvent {
  public final double[] gyro = new double[3]; // [deg/s]
  public final double[] temp = new double[3]; // [deg C]
  public final double[] accx = new double[3]; // [G] TODO should convert to 9.81 m/s ?
  public final double[] accy = new double[3]; // [G]
  /** gps timestamp from top of hour */
  int gps_usec;
  String nmea;

  public int gps_usec() {
    return gps_usec;
  }

  public String nmea() {
    return nmea;
  }

  public void print() {
    System.out.println(Tensors.vectorDouble(gyro).map(Round._2));
    System.out.println(Tensors.vectorDouble(temp).map(Round._2));
    System.out.println(Tensors.vectorDouble(accx).map(Round._2));
    System.out.println(Tensors.vectorDouble(accy).map(Round._2));
    System.out.println(nmea);
    System.out.println(gps_usec);
  }
}
