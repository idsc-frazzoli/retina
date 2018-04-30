// code by jph
package ch.ethz.idsc.gokart.gui;

public enum GokartLcmChannel {
  ;
  public static final String URG04LX_FRONT = "front";
  public static final String VLP16_CENTER = "center";
  public static final String DAVIS_OVERVIEW = "overview";
  public static final String JOYSTICK = "generic_xbox_pad";
  /** absolute steering column position {@link GokartStatusEvent} */
  public static final String STATUS = "gokart.status.get";
  /** {x, y, heading, quality} */
  public static final String POSE_LIDAR = "gokart.pose.lidar";
  // ---
  /** for debugging of rimo rate controller */
  public static final String RIMO_CONTROLLER_PI = "rimo.controller.pi";
}
