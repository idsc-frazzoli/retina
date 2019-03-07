// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx;

/** the Urg04lx has a field of view of 240 degree
 * 0.1[s] == 100[ms] per scan
 * 
 * The angular resolution is 360/1024 degree == 0.351563 degree.
 * at a field of view of 240 degree this results in 682 points
 * 
 * Detection range is from 20[mm] == 2[cm] == 0.02[m]
 * up to 5600[mm] == 5.6[m]
 * 
 * For distances above 1[m], the accuracy is ±3%.
 * 
 * https://www.hokuyo-aut.jp/search/single.php?serial=166 */
public enum Urg04lxDevice {
  ;
  /** maximum number of usable range measurements per rotation
   * the API is not documented. 682 is returned by the function
   * int n = urg_get_distance(&urg, data, &time_stamp);
   * 682 is assumed even though we would expect an odd number of samples */
  public static final int MAX_POINTS = 682;
  /** start of field of view in degree */
  public static final int FOV_LO = -120;
  /** end of field of view in degree */
  public static final int FOV_HI = +120;

  public static String channel(String lidarId) {
    return "urg04lx." + lidarId + ".ray";
  }
}
