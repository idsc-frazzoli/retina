// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
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
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

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

  public Tensor frontWheelVelocity() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      if (lidarLocalizationModule != null) {
        AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(steerColumnTracker.getSteerColumnEncoderCentered());
        {
          Tensor velocity = lidarLocalizationModule.getVelocity();
          Tensor frontLeftVel = axleConfiguration.wheel(0).adjoint(velocity);
          // TODO prevent division by zero
          // Scalar slip = frontLeftVel.Get(1).divide(frontLeftVel.Get(0)); // vy == side slip
          // Scalar force = new Pacejka3(5, 3).apply(slip);
          Tensor frontRightVel = axleConfiguration.wheel(1).adjoint(velocity);
          new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(0.5)); // 1 means unfiltered
        }
        Scalar zero = Quantity.of(0, SI.METER);
        Scalar minus1 = RealScalar.of(-1);
        Tensor velocity = lidarLocalizationModule.getVelocity().extract(0, 2);
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
        Tensor frontTireVelocityLeft = velocity.add(crossProductLeft);
        Tensor frontTireVelocityRight = velocity.add(crossProductRight);
        // angle between front and velocity
        Scalar angleSCE = steerColumnTracker.getSteerColumnEncoderCentered();
        Scalar angleGrad = steerMapping.getAngleFromSCE(angleSCE);
        Tensor pair = ChassisGeometry.GLOBAL.getAckermannSteering().pair(angleGrad);
        Scalar angleL = pair.Get(0);
        Scalar angleR = pair.Get(1);
        // convert the angle of the wheel into a vector pointing in the direction of the wheel
        Scalar leftWheelx = Sin.FUNCTION.apply(angleL);
        Scalar leftWheely = Cos.FUNCTION.apply(angleL);
        Scalar rightWheelx = Sin.FUNCTION.apply(angleR);
        Scalar rightWheely = Cos.FUNCTION.apply(angleR);
        // Vector which points in the direction of the wheel
        Tensor vectorWheelL = Tensors.of(leftWheelx, leftWheely);
        Tensor vectorWheelR = Tensors.of(rightWheelx, rightWheely);
        Tensor lateralVelocityLeft = Cross.of(vectorWheelL, frontTireVelocityLeft);
        Tensor lateralVelocityRight = Cross.of(vectorWheelR, frontTireVelocityRight);
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
