// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

public enum Hdl32eLcmChannels {
  ;
  public static String ray(String lidarId) {
    return "hdl32e." + lidarId + ".ray";
  }

  public static String pos(String lidarId) {
    return "hdl32e." + lidarId + ".pos";
  }
}
