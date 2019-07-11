// code by mh, jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Reference: Marc Heim Thesis, p. 14 */
public class AngularSlip {
  /** tangentSpeed with unit m*s^-1 */
  private final Scalar tangentSpeed;
  /** rotationPerMeterDriven with unit m^-1
   * usually taken from steering model, for instance:
   * <pre>
   * steerMapping.getRatioFromSCE(steerColumnInterface);
   * </pre> */
  private final Scalar rotationPerMeterDriven;
  /** wantedRotationRate with unit s^-1 */
  private final Scalar wantedRotationRate;
  /** gyroZ with unit s^-1 */
  private final Scalar gyroZ;

  /** @param tangentSpeed m*s^-1
   * @param rotationPerMeterDriven m^-1
   * @param gyroZ s^-1 */
  public AngularSlip(Scalar tangentSpeed, Scalar rotationPerMeterDriven, Scalar gyroZ) {
    this.tangentSpeed = tangentSpeed;
    this.rotationPerMeterDriven = rotationPerMeterDriven;
    wantedRotationRate = rotationPerMeterDriven.multiply(tangentSpeed); // unit s^-1
    this.gyroZ = gyroZ;
  }

  /** Hint: the lateral component of velocity vy[m*s^-1] is ignored
   * 
   * @param velocity {vx[m*s^-1], vy[m*s^-1], gyroZ[s^-1]}
   * @param rotationPerMeterDriven [m^-1] */
  public AngularSlip(Tensor velocity, Scalar rotationPerMeterDriven) {
    this(velocity.Get(0), rotationPerMeterDriven, velocity.Get(2));
  }

  /** @return tangentSpeed with unit m*s^-1 */
  public Scalar tangentSpeed() {
    return tangentSpeed;
  }

  /** @return rotationPerMeterDriven with unit m^-1 */
  public Scalar rotationPerMeterDriven() {
    return rotationPerMeterDriven;
  }

  /** @return wantedRotationRate with unit s^-1 */
  public Scalar wantedRotationRate() {
    return wantedRotationRate;
  }

  /** @return gyroZ with unit s^-1 */
  public Scalar gyroZ() {
    return gyroZ;
  }

  /** @return difference between wantedRotationRate and gyroZ */
  public Scalar angularSlip() {
    return wantedRotationRate.subtract(gyroZ);
  }

  /** @return toString Function return gyroZ */
  @Override
  public String toString() {
    return Tensors.of( //
        tangentSpeed, //
        rotationPerMeterDriven, //
        wantedRotationRate, //
        gyroZ).toString();
  }
}
