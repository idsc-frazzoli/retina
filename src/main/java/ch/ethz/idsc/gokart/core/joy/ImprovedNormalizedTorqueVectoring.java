// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.core.mpc.PowerLookupTable;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

public class ImprovedNormalizedTorqueVectoring extends ImprovedTorqueVectoring {
  PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();

  public ImprovedNormalizedTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override
  public Tensor powers(Scalar expectedRotationPerMeterDriven, Scalar meanTangentSpeed, Scalar angularSlip, Scalar power, Scalar realRotation) {
    // wrapper for torque vectoring method
    Scalar wantedAcceleration = powerLookupTable.getNormalizedAccelerationTorqueCentered(power, meanTangentSpeed);
    Tensor motorCurrents = getMotorCurrentsFromAcceleration(//
        expectedRotationPerMeterDriven, //
        meanTangentSpeed, //
        angularSlip, //
        wantedAcceleration, //
        realRotation);
    return motorCurrents.divide(Quantity.of(2300, NonSI.ARMS));
  }

  /** get torque vectoring motor currents corresponding to the wanted rotation speed
   * (this can also be used externally)
   * @param expectedRotationPerMeterDriven [1/s]
   * @param meanTangentSpeed [m/s]
   * @param angularSlip [1/s]
   * @param wantedAcceleration [m/s^2]
   * @param realRotation [1/s]
   * @return the motor currents [Arms] */
  public Tensor getMotorCurrentsFromAcceleration(Scalar expectedRotationPerMeterDriven, Scalar meanTangentSpeed, Scalar angularSlip, Scalar wantedAcceleration,
      Scalar realRotation) {
    Scalar dynamicComponent = getDynamicComponent(angularSlip);
    Scalar staticComponent = getStaticComponent(expectedRotationPerMeterDriven, meanTangentSpeed);
    // ---
    Scalar wantedZTorque = wantedZTorque( //
        dynamicComponent.add(staticComponent), // One
        realRotation);
    // left and right power prefer power over Z-torque
    return getAdvancedMotorCurrents(wantedAcceleration, wantedZTorque, meanTangentSpeed);
  }

  /** @param wantedAcceleration [m(s^2]
   * @param wantedZTorque [ONE] TODO: currently dimensionless. Should we change that?
   * @param velocity [m/s]
   * @return the required motor currents [Arms] */
  private Tensor getAdvancedMotorCurrents(Scalar wantedAcceleration, Scalar wantedZTorque, Scalar velocity) {
    Tensor MinMax = powerLookupTable.getMinMaxAcceleration(velocity);
    Scalar min = MinMax.Get(0);
    Scalar max = MinMax.Get(1);
    Scalar halfRange = max.subtract(min).divide(Quantity.of(2, SI.ONE));
    Scalar mid = (Scalar) Mean.of(MinMax);
    // get acceleration remapped to [-1,1] TODO: find handy Tensor function
    Scalar remappedMeanAcceleration = //
        wantedAcceleration.subtract(mid).divide(halfRange);//
    // get clipped individual accelerations
    Tensor remappedAccelerations = clip(//
        remappedMeanAcceleration.subtract(wantedZTorque), //
        remappedMeanAcceleration.add(wantedZTorque));
    // remap again to acceleration space TODO: find handy Tensor function
    // TODO: do something like this (doesn't seem to work)
    // Tensor wantedAccelerations = remappedAccelerations.map(x -> x.add(mid).multiply(halfRange));//
    Tensor wantedAccelerations = Tensors.of(//
        remappedAccelerations.Get(0).multiply(halfRange).add(mid), //
        remappedAccelerations.Get(1).multiply(halfRange).add(mid));
    return Tensors.of(//
        powerLookupTable.getNeededCurrent(wantedAccelerations.Get(0), velocity), //
        powerLookupTable.getNeededCurrent(wantedAccelerations.Get(1), velocity));
  }
}
