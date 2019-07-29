// code by mg
package ch.ethz.idsc.retina.app.slam;

import ch.ethz.idsc.retina.util.sys.AppResources;

/** These values are not parameters but physical limits */
public class SlamVehicleLimit {
  public static final SlamVehicleLimit GLOBAL = AppResources.load(new SlamVehicleLimit());
  // ---
  public final double LINVEL_MIN = 0; // "m/s"
  public final double LINVEL_MAX = 8; // "m/s"
  public final double LINACCEL_MIN = -2.5; // "m/s²"
  public final double LINACCEL_MAX = 2.5; // "m/s²"
  public final double ANGACCEL_MIN = -10; // "rad/s²"
  public final double ANGACCEL_MAX = 10; // "rad/s²"
}
