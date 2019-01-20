// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Clip;

public class SimpleTorqueVectoring implements TorqueVectoringInterface {
  final TorqueVectoringConfig torqueVectoringConfig;

  public SimpleTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
  }

  @Override // from TorqueVectoringInterface
  public Tensor powers(Scalar expectedRotationPerMeterDriven, Scalar meanTangentSpeed, Scalar angularSlip, Scalar wantedPower, Scalar realRotation) {
    // compute differential torque (in ARMS as we do not use the power function yet)
    // Scalar dynamicComponent = angularSlip.multiply(torqueVectoringConfig.dynamicCorrection);
    Scalar dynamicComponent = getDynamicComponent(angularSlip);
    // Scalar lateralAcceleration = Times.of(expectedRotationPerMeterDriven, meanTangentSpeed, meanTangentSpeed);
    // Scalar staticComponent = lateralAcceleration.multiply(torqueVectoringConfig.staticCompensation);
    Scalar staticComponent = getStaticComponent(expectedRotationPerMeterDriven, meanTangentSpeed);
    // ---
    Scalar wantedZTorque = wantedZTorque( //
        dynamicComponent.add(staticComponent), // One
        realRotation);
    // left and right power prefer power over Z-torque
    Scalar power = Clip.absoluteOne().apply(wantedPower);
    return TorqueVectoringClip.of( //
        power.subtract(wantedZTorque), // unit one
        power.add(wantedZTorque) // unit one
    );
  }

  /** @param angularSlip [1/s]
   * @return dynamic component [1] */
  final Scalar getDynamicComponent(Scalar angularSlip) {
    return angularSlip.multiply(torqueVectoringConfig.dynamicCorrection);
  }

  /** @param angularSlip [1/s]
   * @return dynamic component [1] */
  final Scalar getStaticComponent(Scalar expectedRotationPerMeterDriven, Scalar meanTangentSpeed) {
    Scalar lateralAcceleration = Times.of( //
        expectedRotationPerMeterDriven, //
        meanTangentSpeed, //
        meanTangentSpeed);
    return lateralAcceleration.multiply(torqueVectoringConfig.staticCompensation);
  }

  /** @param wantedZTorque TODO MH state unit
   * @param realRotation TODO MH state unit
   * @return quantity with unit same as wantedZTorque */
  Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation) {
    return wantedZTorque; // simple implementation
  }
}
