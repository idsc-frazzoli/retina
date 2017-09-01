// code by jph
package ch.ethz.idsc.retina.dev.lidar.mark8;

public enum Mark8Device {
  ;
  public static final int TCP_PORT = 4141; // 4141
  // ---
  public static final int HEADER = 0x75bd7e97;
  public static final int LENGTH = 6632;
  // ---
  public static String channel(String lidarId) {
    return "mark8." + lidarId + ".ray";
  }
}
