// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

public enum Hdl32eLcmChannels {
  ;
  public static String firing(String lidarId) {
    return "hdl32e." + lidarId + ".fir";
  }

  public static String positioning(String lidarId) {
    return "hdl32e." + lidarId + ".pos";
  }
}
