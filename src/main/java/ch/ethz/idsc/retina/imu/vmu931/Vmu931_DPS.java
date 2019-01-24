// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

/** resolution of gyroscope in degree per second */
public enum Vmu931_DPS {
  _250, //
  _500, //
  _1000, //
  _2000, //
  ;
  private final int mask = 1 << (ordinal() + 4);
  private final byte command = (byte) (48 + ordinal());

  public boolean isActive(byte resolution) {
    return (resolution & mask) == mask;
  }

  public byte[] set() {
    return Vmu931Statics.command(command);
  }
}
