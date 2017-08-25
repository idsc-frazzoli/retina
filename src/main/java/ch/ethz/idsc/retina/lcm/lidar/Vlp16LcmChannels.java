// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

public enum Vlp16LcmChannels {
  ;
  public static String ray(String lidarId) {
    return "vlp16." + lidarId + ".ray";
  }

  public static String pos(String lidarId) {
    return "vlp16." + lidarId + ".pos";
  }
}
