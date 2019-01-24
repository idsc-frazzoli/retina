// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

public enum Vmu931Statics {
  ;
  /** accelerometer */
  static final byte ID_ACCELEROMETER = 'a';
  /** gyroscope */
  static final byte ID_GYROSCOPE = 'g';
  /** magnetometer */
  static final byte ID_MAGNETOMETER = 'c';
  /** quaternion */
  static final byte ID_QUATERNION = 'q';
  /** euler angle */
  static final byte ID_EULER_ANGLES = 'e';
  /** heading */
  static final byte ID_HEADING = 'h';
  /** self test */
  private static final byte ID_SELFTEST = 't';
  /** status of sensor
   * results in a reply of size == 11 */
  static final byte ID_STATUS = 's';

  private static byte[] command(byte type) {
    return new byte[] { 'v', 'a', 'r', type };
  }

  public static byte[] requestStatus() {
    return command(ID_STATUS);
  }
}
