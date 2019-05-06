// code by mh, jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.Scalar;

public class AngularSlip {
  private final Scalar tangentSpeed;
  private final Scalar rotationPerMeterDriven;
  private final Scalar wantedRotationRate;
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

  /** USE ONLY FOR TESTS !!! BECAUSE wantedRotationRate
   * SHOULD BE COMPUTED FROM OTHER TWO PARAMETERS:
   * wantedRotationRate = rotationPerMeterDriven.multiply(tangentSpeed);
   * 
   * @param tangentSpeed m*s^-1
   * @param rotationPerMeterDriven m^-1
   * @param wantedRotationRate [== rotationPerMeterDriven.multiply(tangentSpeed)]
   * @param gyroZ */
  // TODO JPH this constructor should be made obsolete
  public AngularSlip(Scalar tangentSpeed, Scalar rotationPerMeterDriven, Scalar wantedRotationRate, Scalar gyroZ) {
    this.tangentSpeed = tangentSpeed;
    this.rotationPerMeterDriven = rotationPerMeterDriven;
    this.wantedRotationRate = wantedRotationRate; // unit s^-1
    this.gyroZ = gyroZ;
  }

  public Scalar tangentSpeed() {
    return tangentSpeed;
  }

  public Scalar rotationPerMeterDriven() {
    return rotationPerMeterDriven;
  }

  public Scalar wantedRotationRate() {
    return wantedRotationRate;
  }

  public Scalar gyroZ() {
    return gyroZ;
  }

  public Scalar angularSlip() {
    return wantedRotationRate.subtract(gyroZ);
  }
}
