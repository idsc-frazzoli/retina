// code by mh
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerMapping;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Tan;

public class TorqueVectoringJoystickModule extends GuideJoystickModule<RimoPutEvent> //
    implements RimoGetListener {
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final SimpleTorqueVectoring simpleTorqueVectoring = new SimpleTorqueVectoring(TorqueVectoringConfig.GLOBAL);
  private Scalar meanTangentSpeed = Quantity.of(0, SI.VELOCITY);

  @Override // from AbstractModule
  void protected_first() {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(this);
  }

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick) {
    Scalar theta = steerMapping.getAngleFromSCE(steerColumnInterface); // steering angle of imaginary front wheel
    Scalar rotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // m^-1
    // why isn't theta rad/m?
    Scalar power = Differences.of(joystick.getAheadPair_Unit()).Get(0); // unitless in the interval [-1, 1]
    // Scalar constant3 = RealScalar.of(-0.58);
    // Scalar constant1 = RealScalar.of(0.98);
    // compute wanted motor torques / no-slip behavior (sorry jan for corrective factor)
    Scalar wantedRotationRate = rotationPerMeterDriven.multiply(meanTangentSpeed); // unit s^-1
    // compute (negative) angular slip
    Scalar gyroZ = DavisImuTracker.INSTANCE.getGyroZ();
    Scalar angularSlip = wantedRotationRate.subtract(gyroZ);
    // ---
    Tensor powers = simpleTorqueVectoring.powers(rotationPerMeterDriven, meanTangentSpeed, angularSlip, power);
    Tensor torquesARMS = powers.multiply(JoystickConfig.GLOBAL.torqueLimit); // vector of length 2
    // ---
    short arms_rawL = Magnitude.ARMS.toShort(torquesARMS.Get(0));
    short arms_rawR = Magnitude.ARMS.toShort(torquesARMS.Get(1));
    System.out.println("arms_rawl: " + arms_rawL + " arms_rawr " + arms_rawR);
    return Optional.of(RimoPutHelper.operationTorque( //
        (short) -arms_rawL, // sign left invert
        (short) +arms_rawR // sign right id
    ));
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent getEvent) {
    meanTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
  }
}
