// code by jph
package ch.ethz.idsc.gokart.gui;

public enum GokartLcmChannel {
  ;
  public static final String URG04LX_FRONT = "front";
  public static final String VLP16_CENTER = "center";
  public static final String DAVIS_OVERVIEW = "overview";
  public static final String JOYSTICK = "joystick.generic_xbox_pad";
  /** absolute steering column position {@link GokartStatusEvent} */
  public static final String STATUS = "gokart.status.get";
  /** {x, y, heading, quality} */
  public static final String POSE_LIDAR = "gokart.pose.lidar";
  /** current trajectory infos from GokartTrajectoryModule
   * message is self contained: state, time, flow */
  public static final String TRAJECTORY_XYAT_STATETIME = "gokart.trajectory.xyat";
  /** current trajectory infos from GokartTrajectorySRModule
   * message is self contained: state, time, flow */
  public static final String TRAJECTORY_XYAVT_STATETIME = "gokart.trajectory.xyavt";
  // ---
  /** for debugging of rimo rate controller */
  public static final String RIMO_CONTROLLER_PI = "rimo.controller.pi";
  /** for debugging of stable rimo rate controller */
  public static final String RIMO_CONTROLLER_AW = "rimo.controller.aw";
}
