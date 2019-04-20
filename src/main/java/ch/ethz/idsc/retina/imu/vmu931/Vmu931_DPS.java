// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

/** resolution of gyroscope in degree per second */
public enum Vmu931_DPS implements Vmu931Resolution {
  _250, //
  _500, //
  _1000, //
  _2000, //
  ;
  private final int mask = 1 << (ordinal() + 4);
  private final byte command = (byte) (48 + ordinal());

  @Override // from Vmu931Resolution
  public boolean isActive(byte value) {
    return (value & mask) == mask;
  }

  @Override // from Vmu931Resolution
  public byte[] setActive() {
    return Vmu931Statics.command(command);
  }
}
