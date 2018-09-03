// code by mg
package ch.ethz.idsc.demo.mg.slam;

/** TODO put in same format as e.g. PursuitConfig or SteerConfig, or include the parameters
 * in such a file */
// These values are not parameters but physical limits
public enum VehicleConfig {
  ;
  public static final double LINVEL_MIN = 0; // "m/s"
  public static final double LINVEL_MAX = 8; // "m/s"
  public static final double LINACCEL_MIN = -2.5; // "m/s²"
  public static final double LINACCEL_MAX = 2.5; // "m/s²"
  public static final double ANGACCEL_MIN = -10; // "rad/s²"
  public static final double ANGACCEL_MAX = 10; // "rad/s²"
}
