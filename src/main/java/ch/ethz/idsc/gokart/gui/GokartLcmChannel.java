// code by jph
package ch.ethz.idsc.gokart.gui;

public enum GokartLcmChannel {
  ;
  public static final String URG04LX_FRONT = "front";
  public static final String VLP16_CENTER = "center";
  public static final String DAVIS_OVERVIEW = "overview";
  public static final String SEYE_OVERVIEW = "overview";
  /** the labjack u3 substitutes the joystick */
  public static final String LABJACK_U3_ADC = "labjack.u3.adc";
  /** primary imu */
  public static final String VMU931_AG = "vmu931.ag";
  /** backup imu */
  public static final String VMU932_AG = "vmu932.ag";
  /** absolute steering column position {@link GokartStatusEvent} */
  public static final String STATUS = "gokart.status.get";
  /** {x, y, heading, quality} */
  public static final String POSE_LIDAR = "gokart.pose.lidar";
  /** {dotX, dotY, angularVelocity} */
  public static final String VELOCITY_FUSION = "gokart.pose.vel";
  /** current trajectory infos from PureTrajectoryModule
   * message is self contained: state, time, flow */
  public static final String TRAJECTORY_XYAT_STATETIME = "gokart.trajectory.xyat";
  /** current trajectory infos from GokartTrajectorySRModule
   * message is self contained: state, time, flow */
  public static final String TRAJECTORY_XYAVT_STATETIME = "gokart.trajectory.xyavt";
  public static final String PURSUIT_CURVE_SE2 = "pursuit.curve.se2";
  public static final String PURSUIT_PLAN = "pursuit.plan";
  // ---
  public static final String MPC_FORCES_CNS = "mpc.forces.cns";
  // ---
  /** for debugging of rimo rate controller */
  public static final String RIMO_CONTROLLER_PI = "rimo.controller.pi";
  /** for debugging of rimo rate controller */
  public static final String RIMO_CONTROLLER_LT = "rimo.controller.lt";
  /** for debugging of stable rimo rate controller */
  public static final String RIMO_CONTROLLER_AW = "rimo.controller.aw";
  /** for finding parameters of antilockbrake module */
  public static final String LINMOT_ANTILOCK = "linmot.antilock";
  public static final String STEER_VIBRATE = "steer.vibrate";

  /***************************************************/
  /** lcm self test contains the string "lcm self test" as bytes */
  public static final String LCM_SELF_TEST = "LCM_SELF_TEST";
  /** the joystick was in use until end of 2018 after which
   * the labjack u3 was available for readout of the throttle */
  public static final String JOYSTICK = "joystick.generic_xbox_pad";
}
