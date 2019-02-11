// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Tan;

/* package */ class MPCActiveCompensationLearning extends MPCControlUpdateListenerWithAction implements RimoGetListener {
  private final static MPCActiveCompensationLearning INSTANCE = new MPCActiveCompensationLearning();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();

  public static MPCActiveCompensationLearning getInstance() {
    return INSTANCE;
  }

  private boolean running = false;
  private ControlAndPredictionSteps lastCNS = null;
  private IntervalClock updateClock = new IntervalClock();
  private IntervalClock rimoClock = new IntervalClock();
  private Scalar lastTangentSpeed = Quantity.of(0, SI.VELOCITY);
  private final GeodesicIIR1Filter accelerationFilter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(.05));
  private Scalar rimoAcceleration = Quantity.of(0, SI.ACCELERATION);
  private Scalar realRotationRate = Quantity.of(0, SI.PER_SECOND);
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final Vmu931ImuFrameListener vmu931ImuFrameListener = new Vmu931ImuFrameListener() {
    @Override
    public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
      realRotationRate = SensorsConfig.GLOBAL.vmu931GyroZ(vmu931ImuFrame);
    }
  };
  private final static Scalar BRAKINGTHRESHOLD = Quantity.of(-1, SI.ACCELERATION);
  private final static Scalar MINVAL = Quantity.of(0.5, SI.ONE);
  Scalar steeringCorrection = RealScalar.ONE;
  Scalar brakingCorrection = RealScalar.ONE;

  @Override // from MPCControlUpdateListenerWithAction
  void doAction() {
    double seconds = updateClock.seconds();
    Scalar deltaT = Quantity.of(seconds, SI.SECOND);
    RimoSocket.INSTANCE.getClass();
    if (Objects.nonNull(lastCNS) && running) {
      boolean linmotControlled = LinmotSocket.INSTANCE.getPutProviderDesc().equals(MPCLinmotProvider.class.getSimpleName());
      boolean rimoControlled = RimoSocket.INSTANCE.getPutProviderDesc().equals(MPCRimoProvider.class.getSimpleName());
      Scalar wantedAcceleration = lastCNS.steps[0].control.getaB();
      if (rimoControlled && linmotControlled && Scalars.lessThan(wantedAcceleration, BRAKINGTHRESHOLD)) {
        // correct
        Scalar accelerationError = rimoAcceleration.subtract(wantedAcceleration);
        correctNegativeAcceleration(accelerationError, wantedAcceleration, deltaT);
        brakingCorrection = Max.of(MINVAL, brakingCorrection);
        brakingCorrection = MPCActiveCompensationLearningConfig.GLOBAL.fixedCorrection;
        System.out.println("error: " + accelerationError + "corrected: " + brakingCorrection);
      }
      // Scalar
      boolean steeringControlled = SteerSocket.INSTANCE.getPutProviderDesc().equals("mpc");
      if (steeringControlled) {
        Scalar theta = steerMapping.getAngleFromSCE(steerColumnInterface); // steering angle of imaginary front wheel
        Scalar rotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // m^-1
        Scalar wantedRotationRate = rotationPerMeterDriven.multiply(lastTangentSpeed); // unit s^-1
        Scalar rotationRateError = realRotationRate.subtract(wantedRotationRate);
        correctSteering(rotationRateError, rotationPerMeterDriven, deltaT);
      }
      //
    }
    lastCNS = cns;
  }

  /** correct the negative acceleration
   * @param accelerationError [m*s^-2]
   * @param absoluteAcceleration [m*s^-2]
   * @param deltaT [s] */
  private void correctNegativeAcceleration(Scalar accelerationError, Scalar absoluteAcceleration, Scalar deltaT) {
    Scalar correctionRate = MPCActiveCompensationLearningConfig.GLOBAL.negativeAccelerationCorrectionRate;
    Scalar correctionStep = accelerationError.negate().multiply(absoluteAcceleration).multiply(deltaT).multiply(correctionRate);
    brakingCorrection = brakingCorrection.add(correctionStep);
  }

  /** correct steering
   * @param rotationVelocityError [s^-1]
   * @param absoluteRotationPerMeterDriven [m^-1]
   * @param deltaT [s] */
  private void correctSteering(Scalar rotationVelocityError, Scalar absoluteRotationPerMeterDriven, Scalar deltaT) {
    Scalar correctionStep = rotationVelocityError.negate().multiply(absoluteRotationPerMeterDriven).multiply(deltaT);
    steeringCorrection = steeringCorrection.add(correctionStep);
  }

  private void start() {
    vmu931ImuLcmClient.addListener(vmu931ImuFrameListener);
    vmu931ImuLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addGetListener(this);
    running = true;
  }

  private void stop() {
    vmu931ImuLcmClient.stopSubscriptions();
    RimoSocket.INSTANCE.removeGetListener(this);
    running = false;
  }

  // FIXME do this the right way
  public void setActive(boolean active) {
    if (!running & active) {
      System.out.println("Active compensation started");
      start();
    }
    if (running & !active) {
      System.out.println("Active compensation stopped");
      stop();
    }
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent getEvent) {
    Scalar currentTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
    Scalar acceleration = currentTangentSpeed//
        .subtract(lastTangentSpeed)//
        .divide(Quantity.of(rimoClock.seconds(), SI.SECOND));
    rimoAcceleration = (Scalar) accelerationFilter.apply(acceleration);
    lastTangentSpeed = currentTangentSpeed;
  }
}
