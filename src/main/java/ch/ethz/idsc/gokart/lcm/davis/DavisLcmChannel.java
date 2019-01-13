// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

public enum DavisLcmChannel {
  IMU, //
  DVS, //
  RST, //
  SIG, //
  ;
  public final String extension = "." + name().toLowerCase();
}
