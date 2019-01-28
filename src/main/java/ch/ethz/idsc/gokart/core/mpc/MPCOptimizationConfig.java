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
  @FieldSubdivide(start = "1f[m*s^-2]", end = "40[m*s^-2]", intervals = 100)
  public Scalar maxAcceleration = Quantity.of(30, SI.ACCELERATION);
  /** The limit for the Go-kart speed */
  @FieldSubdivide(start = "4f[m*s^-1]", end = "10[m*s^-1]", intervals = 6)
  public Scalar maxSpeed = Quantity.of(10, SI.VELOCITY);
  /** The limit for the Go-kart longitudonal acceleration */
  @FieldSubdivide(start = "3f[m*s^-2]", end = "15[m*s^-2]", intervals = 48)
  public Scalar maxLatAcc = Quantity.of(8, SI.ACCELERATION);
  /** The limit for the Go-kart lateral acceleration */
  @FieldSubdivide(start = "3f[m*s^-2]", end = "15[m*s^-2]", intervals = 48)
  public Scalar maxLonAcc = Quantity.of(5, SI.ACCELERATION);
  /** The maximum corrected acceleration value for the front axle (to avoid understeering) */
  @FieldSubdivide(start = "3f[m*s^-2]", end = "15[m*s^-2]", intervals = 48)
  public Scalar latAccLim = Quantity.of(5, SI.ACCELERATION);
  /** The maximum corrected acceleration value for the front axle (to avoid understeering)
   * Why is this m? -> Acceleration/Rotational Acceleration -> m/s^2/(1/s^2)=m */
  @FieldSubdivide(start = "0f[m]", end = "5f[m]", intervals = 50)
  public Scalar rotAccEffect = Quantity.of(1, SI.METER);
  /** the amount of additional front acceleration capacity the torque vectoring can add */
  @FieldSubdivide(start = "0f[m*s^-2]", end = "10f[m*s^-2]", intervals = 50)
  public Scalar torqueVecEffect = Quantity.of(0, SI.ACCELERATION);
  /** the amount of additional front acceleration capacity strong braking can have
   * suggestion: low priority (braking can be done in a straight line */
  @FieldSubdivide(start = "0f[1]", end = "2[1]", intervals = 20)
  public Scalar brakeEffect = Quantity.of(0, SI.ONE);
  /** The mpc update cycle time when not successful */
  public Scalar updateCycle = Quantity.of(0.2, SI.SECOND);
  /** The wait time after a successful optimization */
  public Scalar updateDelay = Quantity.of(0.0, SI.SECOND);
  /** Steering anti-lag */
  public Scalar steerAntiLag = Quantity.of(0.4, SI.SECOND);
  /** Braking anti-lag */
  public Scalar brakingAntiLag = Quantity.of(0.1, SI.SECOND);
  /** Padding */
  @FieldSubdivide(start = "0f[m]", end = "2[m]", intervals = 20)
  public Scalar padding = Quantity.of(1, SI.METER);
  @FieldSubdivide(start = "0f", end = "1", intervals = 10)
  public Scalar qpFactor = Quantity.of(1, SI.ONE);
}
