// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.qty.Quantity;

import ch.ethz.idsc.tensor.red.Min;

public class PowerSteeringModule extends AbstractModule implements SteerGetListener, SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();

  @Override
  protected void first() {
    SteerSocket.INSTANCE.addGetListener(this);
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removeGetListener(this);
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  private SteerGetEvent prev;
  private double diffRelRckPos;

  @Override
  public void getEvent(SteerGetEvent getEvent) {
    if (prev != null) {
      diffRelRckPos = getEvent.getGcpRelRckPos() - prev.getGcpRelRckPos();
    }
    prev = getEvent;
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      return Optional.of(putEvent(steerColumnTracker.getSteerColumnEncoderCentered()));
    }
    return Optional.empty();
  }

  public Tensor FrontWheelVelocity() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      if (lidarLocalizationModule != null) {
        Scalar zero = Quantity.of(0, SI.METER);
        Scalar minus1 = RealScalar.of(-1);
        Tensor velocity = lidarLocalizationModule.getVelocityXY();
        Scalar angularvelocity = lidarLocalizationModule.getGyroZ();
        Tensor angularvelocityTensor = Tensors.of(zero, zero, angularvelocity);
        // distances from origin to front tire
        Scalar x = ChassisGeometry.GLOBAL.xAxleRtoF;
        Scalar y = ChassisGeometry.GLOBAL.yTireFront;
        Scalar z = zero;
        Tensor distanceOriginRightFrontWheel = Tensors.of(x, y.multiply(minus1), z);
        Tensor distanceOriginLeftFrontWheel = Tensors.of(x, y, z);
        Tensor crossProductLeft = Cross.of(angularvelocityTensor, distanceOriginLeftFrontWheel);
        Tensor crossProductRight = Cross.of(angularvelocityTensor, distanceOriginRightFrontWheel);
        // velocity in the front tire
        Tensor FrontTireVelocityLeft = velocity.add(crossProductLeft);
        Tensor FrontTireVelocityRight = velocity.add(crossProductRight);
        // angle between front and velocity
        Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
        Scalar angleGrad = steerMapping.getAngleFromSCE(angleSCE);
        Tensor pair = ChassisGeometry.GLOBAL.getAckermannSteering().pair(angleGrad);
        Scalar angleL = pair.Get(0);
        Scalar angleR = pair.Get(1);
        // convert the angle of the wheel into a vector pointing in the direction of the wheel
        double angleLDouble = Magnitude.ARMS.toDouble(angleL);
        double angleRDouble = Magnitude.ARMS.toDouble(angleR);
        double projectionLeftSin = Math.sin(angleLDouble);
        double projectionLeftCos = Math.cos(angleLDouble);
        double projectionRightSin = Math.sin(angleRDouble);
        double projectionRightCos = Math.cos(angleRDouble);
        Scalar LeftWheelx = RealScalar.of(projectionLeftSin);
        Scalar LeftWheely = RealScalar.of(projectionLeftCos);
        Scalar RightWheelx = RealScalar.of(projectionRightSin);
        Scalar RightWheely = RealScalar.of(projectionRightCos);
        // Vector which points in the direction of the wheel
        Tensor vectorWheelL = Tensors.of(LeftWheelx, LeftWheely);
        Tensor vectorWheelR = Tensors.of(RightWheelx, RightWheely);
        Tensor lateralVelocityLeft = Cross.of(vectorWheelL, FrontTireVelocityLeft);
        Tensor lateralVelocityRight = Cross.of(vectorWheelR, FrontTireVelocityRight);
        return Tensors.of(lateralVelocityLeft, lateralVelocityRight);
      }
      return Tensors.empty();
    }
    return Tensors.empty();
  }

  public SteerPutEvent putEvent(Scalar currangle) {
    // term1 is the static compensation of the restoring force, depending on the current angle
    // term2 is the compensation depending on the velocity of the steering wheel
    Scalar term1 = currangle.multiply(HapticSteerConfig.GLOBAL.staticCompensation);
    Scalar term2 = Min.of( //
        RealScalar.of(diffRelRckPos).multiply(HapticSteerConfig.GLOBAL.dynamicCompensation), //
        HapticSteerConfig.GLOBAL.dynamicCompensationBoundary);
    return SteerPutEvent.createOn(term1.add(term2));
  }
  // return Optional.of(SteerPutEvent.createOn(Quantity.of(diffRelRckPos > 0 ? 0.3 : -0.3, "SCT")));
  // }
}
