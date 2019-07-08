// code by mg
package ch.ethz.idsc.retina.app.slam;

import ch.ethz.idsc.retina.util.sys.AppResources;

/** TODO MG put in same format as e.g. PurePursuitConfig or SteerConfig, or include the parameters
 * in such a file */
// These values are not parameters but physical limits
public class VehicleConfig {
  public static final VehicleConfig GLOBAL = AppResources.load(new VehicleConfig());
  // ---
  public final double LINVEL_MIN = 0; // "m/s"
  public final double LINVEL_MAX = 8; // "m/s"
  public final double LINACCEL_MIN = -2.5; // "m/s²"
  public final double LINACCEL_MAX = 2.5; // "m/s²"
  public final double ANGACCEL_MIN = -10; // "rad/s²"
  public final double ANGACCEL_MAX = 10; // "rad/s²"
}
