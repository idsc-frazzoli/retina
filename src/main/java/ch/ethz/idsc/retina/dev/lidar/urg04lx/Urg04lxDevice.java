// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

/** the Urg04lx has a field of view of 240 degree
 * 
 * https://www.hokuyo-aut.jp/search/single.php?serial=166 */
public enum Urg04lxDevice {
  ;
  /** maximum number of usable range measurements per rotation */
  // TODO check why this is not an uneven number
  public static final int MAX_POINTS = 682;
  /** start of field of view in degree */
  public static final int FOV_LO = -120;
  /** end of field of view in degree */
  public static final int FOV_HI = +120;

  public static String channel(String lidarId) {
    return "urg04lx." + lidarId + ".ray";
  }
}
