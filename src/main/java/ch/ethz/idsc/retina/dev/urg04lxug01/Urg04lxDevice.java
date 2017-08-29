// code by jph
package ch.ethz.idsc.retina.dev.urg04lxug01;

/** https://www.hokuyo-aut.jp/search/single.php?serial=166 */
public enum Urg04lxDevice {
  ;
  public static String channel(String lidarId) {
    return "urg04lx." + lidarId + ".rng";
  }
}
