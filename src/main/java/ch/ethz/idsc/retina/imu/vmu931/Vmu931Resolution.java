// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

public interface Vmu931Resolution {
  /** @param value
   * @return */
  boolean isActive(byte value);

  /** @return command to configure resolution */
  byte[] setActive();
}
