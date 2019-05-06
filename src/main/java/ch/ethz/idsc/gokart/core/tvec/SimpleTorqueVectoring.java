// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Clips;

public class SimpleTorqueVectoring implements TorqueVectoringInterface {
  final TorqueVectoringConfig torqueVectoringConfig;

  public SimpleTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
  }

  @Override // from TorqueVectoringInterface
  public Tensor powers(AngularSlip angularSlip, Scalar wantedPower) {
    // compute differential torque (in ARMS as we do not use the power function yet)
    Scalar dynamicComponent = getDynamicComponent(angularSlip.angularSlip());
    Scalar staticComponent = getStaticComponent(angularSlip.rotationPerMeterDriven(), angularSlip.tangentSpeed());
    // ---
    Scalar wantedZTorque = wantedZTorque(dynamicComponent.add(staticComponent), angularSlip.gyroZ());
    // left and right power prefer power over Z-torque
    return TorqueVectoringClip.from(Clips.absoluteOne().apply(wantedPower), wantedZTorque);
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
   * @param realRotation s^-1
   * @return quantity with unit same as wantedZTorque */
  Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation) {
    return wantedZTorque; // simple implementation
  }
}
