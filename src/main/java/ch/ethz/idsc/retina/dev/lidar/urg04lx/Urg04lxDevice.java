// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

/** https://www.hokuyo-aut.jp/search/single.php?serial=166 */
public enum Urg04lxDevice {
  ;
  // TODO check why this is not an uneven number
  public static final int POINTS = 682;
  /** start of field of view in degree */
  public static final int FOV_LO = -120;
  /** end of field of view in degree */
  public static final int FOV_HI = +120;

  public static String channel(String lidarId) {
    // the name "rng" indicates that only 2d, i.e. range data is available
    return "urg04lx." + lidarId + ".rng";
  }
}
