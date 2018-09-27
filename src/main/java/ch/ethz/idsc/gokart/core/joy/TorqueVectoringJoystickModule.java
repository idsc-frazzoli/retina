// code by mh
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Tan;

public class TorqueVectoringJoystickModule extends GuideJoystickModule<RimoPutEvent> //
    implements DavisImuFrameListener {
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  // TODO
  Scalar gyro_Z = Quantity.of(0, SI.PER_SECOND);
  private Scalar meanTangentSpeed = Quantity.of(0, SI.VELOCITY);
  // rimo get listener
  final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent getEvent) {
      meanTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
      // Scalar leftRate = getEvent.getTireL.getAngularRate_Y();
      // Scalar rightRate = getEvent.getTireR.getAngularRate_Y();
      // meanRate = leftRate.add(rightRate).divide(Quantity.of(2, SI.ONE));
    }
  };

  @Override // from AbstractModule
  void protected_first() {
    davisImuLcmClient.addListener(this);
    davisImuLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    davisImuLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick) {
    Scalar theta1 = SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface); // steering angle
    Scalar rotPerMeterDriver = Tan.FUNCTION.apply(theta1).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // 1/m
    // why isn't theta rad/m?
    // Scalar theta = SteerConfig.GLOBAL.getSCEfromAngle(angle)
    Scalar power = Differences.of(joystick.getAheadPair_Unit()).Get(0);
    // Scalar constant3 = RealScalar.of(-0.58);
    // Scalar constant1 = RealScalar.of(0.98);
    // Scalar ackermannCenterSteering = constant3.multiply(theta.multiply(theta.multiply(theta))).add(constant1.multiply(theta));
    // compute wanted motor torques / no-slip behavior (sorry jan for corrective factor)
    // Scalar wantedRotationRate =Tan.of(ackermannCenterSteering).multiply(TorqueVectoringConfig.GLOBAL.steeringCorrection.multiply(meanRate));
    Scalar wantedRotationRate = rotPerMeterDriver.multiply(meanTangentSpeed);
    // compute (negative )angular slip
    Scalar angularSlip = wantedRotationRate.subtract(gyro_Z);
    // System.out.println("angular slip: " + angularSlip);
    // compute differential torque (in Arms as we do not use the power function yet)
    Scalar dynamicComponent = angularSlip.multiply(TorqueVectoringConfig.GLOBAL.dynamicCorrection);
    // System.out.println("Dynamic component: " + dynamicComponent);
    Scalar lateralAcceleration = rotPerMeterDriver.multiply(Power.of(meanTangentSpeed, 2));
    // System.out.println("lateral Acceleration: " + lateralAcceleration);
    Scalar staticComponent = lateralAcceleration.multiply(TorqueVectoringConfig.GLOBAL.staticCompensation);
    // System.out.println("Static component: " + staticComponent);
    Scalar wantedZTorque = dynamicComponent.add(staticComponent); // One
    // System.out.println("ZTorque: " + wantedZTorque);
    // left and right power
    Scalar powerLeft = power.subtract(wantedZTorque);// One
    Scalar powerRight = power.add(wantedZTorque);// One
    // prefer power over Z-torque
    Scalar max = Quantity.of(1, SI.ONE);
    Scalar min = Quantity.of(-1, SI.ONE);
    // powerRight = powerRight.add(Clip.absoluteOne().apply(powerLeft).subtract(powerLeft));
    if (Scalars.lessThan(max, powerRight)) {
      Scalar overpower = powerRight.subtract(max);
      powerRight = max;
      powerLeft = powerLeft.add(overpower);
    } else //
    if (Scalars.lessThan(max, powerLeft)) {
      Scalar overpower = powerLeft.subtract(max);
      powerLeft = max;
      powerRight = powerRight.add(overpower);
    } else //
    if (Scalars.lessThan(powerRight, min)) {
      Scalar underPower = powerRight.subtract(min);
      powerRight = min;
      powerLeft = powerLeft.add(underPower);
    } else //
    if (Scalars.lessThan(powerLeft, min)) {
      Scalar underPower = powerLeft.subtract(min);
      powerLeft = min;
      powerRight = powerRight.add(underPower);
    }
    // gyro_Z
    // TorqueVectoringConfig.GLOBAL.brakeDuration
    // Scalar pair = joystick.getAheadPair_Unit().Get(1); // entry in [0, 1]
    powerLeft = Clip.absoluteOne().apply(powerLeft).multiply(JoystickConfig.GLOBAL.torqueLimit);// we can use the same data as from the joystick controller
    powerRight = Clip.absoluteOne().apply(powerRight).multiply(JoystickConfig.GLOBAL.torqueLimit);
    short arms_rawl = Magnitude.ARMS.toShort(powerLeft); // confirm that units are correct
    short arms_rawr = Magnitude.ARMS.toShort(powerRight);
    System.out.println("arms_rawl: " + arms_rawl + " arms_rawr " + arms_rawr);
    return Optional.of(RimoPutHelper.operationTorque( //
        (short) -arms_rawl, // sign left invert
        (short) +arms_rawr // sign right id
    ));
  }

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    gyro_Z = davisImuFrame.gyroImageFrame().Get(1); // TODO magic const
  }
}
