// code by mh
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
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
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Tan;

public class TorqueVectoringJoystickModule extends GuideJoystickModule<RimoPutEvent> //
    implements DavisImuFrameListener {
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private Scalar gyro_Z = Quantity.of(0, SI.PER_SECOND);
  private Scalar meanRate = Quantity.of(0, SI.PER_SECOND);
  // rimo get listener
  final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent getEvent) {
      Scalar leftRate = getEvent.getTireL.getAngularRate_Y();
      Scalar rightRate = getEvent.getTireR.getAngularRate_Y();
      meanRate = leftRate.add(rightRate).divide(Quantity.of(2, SI.ONE));
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
    Scalar theta = SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface); // steering angle
    Scalar power = Differences.of(joystick.getAheadPair_Unit()).Get(0);
    
    Scalar constant3 = Quantity.of(-0.58, SI.ONE);
    Scalar constant1 = Quantity.of(0.98, SI.ONE);
    
    
    Scalar ackermannCenterSteering = constant3.multiply(theta.multiply(theta.multiply(theta))).add(constant1.multiply(theta));
    //compute wanted motor torques / no-slip behavior (sorry jan for corrective factor)
    Scalar wantedRotationRate =Tan.of(ackermannCenterSteering).multiply(TorqueVectoringConfig.GLOBAL.SteeringCorrection.multiply(meanRate));
    //compute (negative )angular slip
    Scalar angularSlip = wantedRotationRate.subtract(gyro_Z);
    System.out.println("angular slip: " + angularSlip);
    //compute differential torque (in Arms as we do not use the power function yet)
    Scalar DynamicComponent = angularSlip.multiply(TorqueVectoringConfig.GLOBAL.DynamicCorrection);
    System.out.println("Dynamic component: " + DynamicComponent);
    Scalar StaticComponent = Tan.of(ackermannCenterSteering).multiply(meanRate.multiply(meanRate.multiply(TorqueVectoringConfig.GLOBAL.StaticCompensation)));
    System.out.println("Static component: " + StaticComponent);
    Scalar wantedZTorque = DynamicComponent.add(StaticComponent);
    System.out.println("ZTorque: " + wantedZTorque);
    //left and right power 
    Scalar powerLeft = power.subtract(wantedZTorque);
    Scalar powerRight = power.add(wantedZTorque);
    //prefer power over Z-torque
    Scalar max = Quantity.of(1, SI.ONE);
    Scalar min = Quantity.of(-1, SI.ONE);
    if(Magnitude.ONE.toDouble(powerRight)>1) {
      Scalar overpower = powerRight.subtract(max);
      powerRight = max;
      powerLeft = powerLeft.add(overpower);
    }else if(Magnitude.ONE.toDouble(powerLeft)>1) {
      Scalar overpower = powerLeft.subtract(max);
      powerLeft = max;
      powerRight = powerRight.add(overpower);
    }else if(Magnitude.ONE.toDouble(powerRight)<-1) {
      Scalar underPower = powerRight.subtract(min);
      powerRight = min;
      powerLeft = powerLeft.add(underPower);
    }else if(Magnitude.ONE.toDouble(powerLeft)<1) {
      Scalar underPower = powerLeft.subtract(min);
      powerLeft = min;
      powerRight = powerRight.add(underPower);
    }
    // gyro_Z
    // TorqueVectoringConfig.GLOBAL.brakeDuration
    // Scalar pair = joystick.getAheadPair_Unit().Get(1); // entry in [0, 1]
    powerLeft = powerLeft.multiply(JoystickConfig.GLOBAL.torqueLimit);//we can use the same data as from the joystick controller
    powerRight = powerRight.multiply(JoystickConfig.GLOBAL.torqueLimit);
    short arms_rawl = Magnitude.ARMS.toShort(powerLeft); // confirm that units are correct
    short arms_rawr = Magnitude.ARMS.toShort(powerRight);
    System.out.println("arms_rawl: " + arms_rawl);
    System.out.println("arms_rawr: " + arms_rawr);
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
