// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

/** https://www.hokuyo-aut.jp/search/single.php?serial=166 */
public enum Urg04lxDevice {
  ;
  public static final int POINTS = 682;
  public static String channel(String lidarId) {
    return "urg04lx." + lidarId + ".rng";
  }
}
