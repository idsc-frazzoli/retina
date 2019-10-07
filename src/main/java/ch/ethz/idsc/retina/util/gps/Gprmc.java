// code by jph
package ch.ethz.idsc.retina.util.gps;

import java.io.Serializable;
import java.util.Arrays;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class Gprmc implements Serializable {
  /** $GPRMC,150038,A,4724.3422,N,00837.9060,E,000.0,052.3,260519,001.8,E,D*14
   * 
   * @param nmea
   * @return */
  public static Gprmc of(String nmea) {
    return new Gprmc(nmea.split(","));
  }

  // ---
  private final String[] split;

  private Gprmc(String[] split) {
    this.split = split;
    if (!split[0].equals("$GPRMC"))
      throw new IllegalArgumentException(Arrays.asList(split).toString());
  }

  public String timestamp() {
    return split[1];
  }

  /** The validity field in the $GPRMC message (‘A’ or ‘V’) should be checked by
   * the user to ensure the GPS system and the VLP-16 are receiving valid
   * Coordinated Universal Time (UTC) updates from the user’s GPS receiver.
   * validity: A=ok, V=invalid
   * 
   * @return */
  public boolean isValid() {
    return split[2].equals("A");
  }

  public String dateStamp() {
    return split[9];
  }

  public Scalar speed() {
    return Quantity.of(Double.parseDouble(split[7]), NonSI.KNOTS);
  }

  public Scalar course() {
    return Quantity.of(Double.parseDouble(split[8]), NonSI.DEGREE_ANGLE);
  }

  private static final double TO_DEGREE_ANGLE = 1E-2;

  /** E W
   * 
   * @return */
  public Scalar gpsX() {
    Scalar scalar = Quantity.of(Double.parseDouble(split[5]) * TO_DEGREE_ANGLE, NonSI.DEGREE_ANGLE);
    return split[6].equals("E") //
        ? scalar
        : scalar.negate();
  }

  /** N S
   * 
   * @return */
  public Scalar gpsY() {
    Scalar scalar = Quantity.of(Double.parseDouble(split[3]) * TO_DEGREE_ANGLE, NonSI.DEGREE_ANGLE);
    return split[4].equals("N") //
        ? scalar
        : scalar.negate();
  }
}
