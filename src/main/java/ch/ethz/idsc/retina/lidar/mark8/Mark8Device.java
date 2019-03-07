// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

/** in order to configure the device Mark8
 * connect the running device to the local network.
 * Then, use a browser to visit http://192.168.1.3:7780 */
public enum Mark8Device {
  ;
  public static final int TCP_PORT = 4141;
  // ---
  public static final int HEADER = 0x75bd7e97;
  public static final int LENGTH = 6632;
  // ---
  public static final int FIRINGS = 50;

  // ---
  public static String channel(String lidarId) {
    return "mark8." + lidarId + ".ray";
  }
}
