// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

public enum Vmu931Channel {
  ACCELEROMETER(Vmu931Statics.ID_ACCELEROMETER), //
  GYROSCOPE(Vmu931Statics.ID_GYROSCOPE), //
  QUATERNION(Vmu931Statics.ID_QUATERNION), //
  MAGNETOMETER(Vmu931Statics.ID_MAGNETOMETER), //
  EULER_ANGLES(Vmu931Statics.ID_EULER_ANGLES), //
  _NA((byte) 0), //
  HEADING(Vmu931Statics.ID_HEADING), //
  ;
  private final byte id;
  private final int mask;

  private Vmu931Channel(byte id) {
    this.id = id;
    mask = 1 << ordinal();
  }

  public boolean isActive(int current) {
    return (current & mask) == mask;
  }

  public byte[] toggle() {
    return new byte[] { 'v', 'a', 'r', id };
  }
}
