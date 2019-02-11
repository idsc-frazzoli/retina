// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.brake.BrakingFunction;
import ch.ethz.idsc.gokart.calib.brake.SelfCalibratingBrakingFunction;
import ch.ethz.idsc.gokart.core.ekf.SimpleVelocityEstimation;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931Channel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class MPCAggressiveCorrectedTorqueVectoringBraking extends MPCBraking implements Vmu931ImuFrameListener, RimoGetListener, StartAndStoppable  {
  private static final Scalar NO_ACCELERATION = Quantity.of(0, SI.ACCELERATION);
  private final MPCOptimizationConfig mpcOptimizationConfig = MPCOptimizationConfig.GLOBAL;
  //private final MPCActiveCompensationLearning activeCompensationLearning = MPCActiveCompensationLearning.getInstance();
  private final SelfCalibratingBrakingFunction brakingFunction = SelfCalibratingBrakingFunction.getInstance();
  private final Vmu931ImuLcmClient vmu931imuLcmClient = new Vmu931ImuLcmClient();
  
  @Override
  public Scalar getBraking(Scalar time) {
    Scalar controlTime = time.add(mpcOptimizationConfig.brakingAntiLag);
    ControlAndPredictionStep cnsStep = getStep(controlTime);
    if (Objects.isNull(cnsStep))
      return RealScalar.ZERO;
    // Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    // Scalar min = (Scalar) Mean.of(minmax);
    // Scalar braking = Max.of(Quantity.of(0, SI.ACCELERATION), cnsStep.control.getaB().negate().add(min));
    Scalar braking = Max.of(NO_ACCELERATION, cnsStep.control.getaB().negate());
    // System.out.println(braking);
    // self calibration
    Scalar gokartSpeed = SimpleVelocityEstimation.getInstance().getVelocity().Get(0).negate();
    Scalar realBraking = currentAcceleration.negate();
    brakingFunction.correctBraking(braking.negate(), realBraking, gokartSpeed, wheelSpeed);
    return brakingFunction.getRelativeBrakeActuation(braking);
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcStateEstimationProvider) {
  }

  @Override
  public void start() {
    vmu931imuLcmClient.addListener(this);
    vmu931imuLcmClient.startSubscriptions();
  }

  @Override
  public void stop() {
    vmu931imuLcmClient.stopSubscriptions();
  }

  private Scalar currentAcceleration;
  @Override
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    currentAcceleration = SensorsConfig.GLOBAL.getAccXY(vmu931ImuFrame).Get(0);
  }

  private Scalar wheelSpeed = Quantity.of(0, SI.VELOCITY);
  @Override
  public void getEvent(RimoGetEvent getEvent) {
    wheelSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
  }
}
