// code by jph
package ch.ethz.idsc.retina.davis;

public enum DavisApsType {
  RST, // reset read
  SIG, // signal read
  DIF, // difference SIG - RST
  ;
  public final String extension = "." + name().toLowerCase();
}
