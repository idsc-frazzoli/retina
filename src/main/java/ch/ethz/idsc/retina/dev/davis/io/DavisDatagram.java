// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

/** collection of constants */
public enum DavisDatagram {
  ;
  /** the choice of ports is arbitrary */
  public static final int DVS_PORT = 14320;
  public static final int APS_PORT = 14321;
  public static final int IMU_PORT = 14322;
  /** number of image columns in an aps block */
  public static final int BLOCK_COLUMNS = 8;
}
