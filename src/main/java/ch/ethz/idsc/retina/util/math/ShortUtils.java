// code by jph
package ch.ethz.idsc.retina.util.math;

public enum ShortUtils {
  ;
  /** @param value
   * @return */
  public static int _24bit(short value) {
    value &= 0x0fff;
    value <<= 4;
    int result = value;
    return result >> 4;
  }
}
