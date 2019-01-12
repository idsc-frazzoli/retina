// code by jph
package ch.ethz.idsc.retina.lidar.hdl32e;

/* package */ enum StaticHelper {
  ;
  /** Application: decode HDL-32E gyro and accelerometer
   * 
   * @param value
   * @return */
  public static int signed24bit(short value) {
    value &= 0x0fff;
    value <<= 4;
    int result = value;
    return result >> 4;
  }
}
