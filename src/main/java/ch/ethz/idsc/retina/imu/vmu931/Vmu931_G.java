// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

/** resolution of gyroscope in g as gravitational constant 9.81[m*s^-2] */
public enum Vmu931_G implements Vmu931Resolution {
  _2, //
  _4, //
  _8, //
  _16, //
  ;
  private final int mask = 1 << ordinal();
  private final byte command = (byte) (52 + ordinal());

  @Override // from Vmu931Resolution
  public boolean isActive(byte value) {
    return (value & mask) == mask;
  }

  @Override // from Vmu931Resolution
  public byte[] setActive() {
    return Vmu931Statics.command(command);
  }
}
