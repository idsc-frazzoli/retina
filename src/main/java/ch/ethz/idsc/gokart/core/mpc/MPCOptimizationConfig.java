// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;

public class MPCOptimizationConfig {
  public static final MPCOptimizationConfig GLOBAL = AppResources.load(new MPCOptimizationConfig());
  /***************************************************/
  /** The limit for the norm of the acceleration */
  @FieldSubdivide(start = "1[m*s^-2]", end = "4[m*s^-2]", intervals = 3)
  public Scalar maxAcceleration = Quantity.of(1.2, SI.ACCELERATION);
  /** The limit for the Go-kart speed */
  @FieldSubdivide(start = "4[m*s^-1]", end = "10[m*s^-1]", intervals = 6)
  public Scalar maxSpeed = Quantity.of(10, SI.VELOCITY);
  /** The limit for the Go-kart longitudonal acceleration */
  public Scalar maxLatAcc = Quantity.of(8, SI.ACCELERATION);
  /** The limit for the Go-kart lateral acceleration */
  public Scalar maxLonAcc = Quantity.of(5, SI.VELOCITY);
  /** The mpc update cycle time when not successful */
  public Scalar updateCycle = Quantity.of(0.2, SI.SECOND);
  /** The wait time after a successful optimization */
  public Scalar updateDelay = Quantity.of(0.0, SI.SECOND);
  /** Steering anti-lag */
  public Scalar steerAntiLag = Quantity.of(0.4, SI.SECOND);
  /** Braking anti-lag */
  public Scalar brakingAntiLag = Quantity.of(0.1, SI.SECOND);
  /** Padding */
  public Scalar padding = Quantity.of(1, SI.METER);
}
