// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import ch.ethz.idsc.retina.lidar.VelodyneModel;

public enum VelodyneLcmChannels {
  ;
  private static String _format(VelodyneModel velodyneModel) {
    return velodyneModel.name().toLowerCase();
  }

  public static String ray(VelodyneModel velodyneModel, String lidarId) {
    return _format(velodyneModel) + "." + lidarId + ".ray";
  }

  public static String pos(VelodyneModel velodyneModel, String lidarId) {
    return _format(velodyneModel) + "." + lidarId + ".pos";
  }
}
