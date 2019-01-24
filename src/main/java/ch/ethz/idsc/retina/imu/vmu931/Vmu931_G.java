// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

public enum Vmu931_G {
  _2, //
  _4, //
  _8, //
  _16, //
  ;
  private final int mask = 1 << ordinal();
  private final byte command = (byte) (52 + ordinal());

  public boolean isActive(byte resolution) {
    return (resolution & mask) == mask;
  }

  public byte[] set() {
    return Vmu931Statics.command(command);
  }
}
