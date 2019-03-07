// code by mh, jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Tan;

/* package */ class GokartAngularSlip implements RimoGetListener {
  private final SteerMapping steerMapping;
  private Scalar meanTangentSpeed = Quantity.of(0, SI.VELOCITY);

  public GokartAngularSlip(SteerMapping steerMapping) {
    this.steerMapping = steerMapping;
  }

  /** @param steerColumnInterface
   * @param gyroZ
   * @return */
  public Scalar getAngularSlip(SteerColumnInterface steerColumnInterface, Scalar gyroZ) {
    Scalar theta = steerMapping.getAngleFromSCE(steerColumnInterface); // steering angle of imaginary front wheel
    // theta has interpretation in rad/m but is encoded in true SI units: "m^-1"
    Scalar rotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // m^-1
    // compute wanted motor torques / no-slip behavior (sorry jan for corrective factor)
    Scalar wantedRotationRate = rotationPerMeterDriven.multiply(meanTangentSpeed); // unit s^-1
    // compute (negative) angular slip
    // Scalar gyroZ = DavisImuTracker.INSTANCE.getGyroZ(); // unit s^-1
    return wantedRotationRate.subtract(gyroZ);
  }

  @Override // from RimoGetListener
  public final void getEvent(RimoGetEvent getEvent) {
    meanTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
  }
}
