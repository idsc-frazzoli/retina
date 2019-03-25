// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

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
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Min;

public class PowerSteeringModule extends AbstractModule implements SteerGetListener, SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final LidarLocalizationModule localisation = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  // private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();

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

  SteerGetEvent prev;
  double diffRelRckPos;

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

  Tensor velocity = localisation.getVelocityXY();
  Scalar angularvelocity = localisation.getGyroZ();
  Scalar zero = Quantity.of(0, SI.METER);
  Scalar minus1 = RealScalar.of(-1);
  Scalar plus1 = RealScalar.of(1);
  // distances from origin to front tire
  Scalar x = ChassisGeometry.GLOBAL.xAxleRtoF;
  Scalar y = ChassisGeometry.GLOBAL.yTireFront;
  Scalar z = zero;
  Tensor angularvelocityTensor = Tensors.of(zero, zero, angularvelocity);
  Tensor distanceOriginFrontwheel = Tensors.of(x, y, z);
  Tensor crossProductLeft = Tensors.of(angularvelocity.multiply(y).multiply(minus1), angularvelocity.multiply(x), zero);
  Tensor crossProductRight = Tensors.of(angularvelocity.multiply(y), angularvelocity.multiply(x), zero);
  // velocity in the front tire
  Tensor FrontTireVelocityLeft = velocity.add(crossProductLeft);
  Tensor FrontTireVelocityRight = velocity.add(crossProductRight);
  // angle between front and velocity

  public SteerPutEvent putEvent(Scalar currangle) {
    // term1 is the static compensation of the restoring force, depending on the current angle
    // term2 is the compensation depending on the velocity of the steering wheel
    Scalar term1 = currangle.multiply(SteerConfig.GLOBAL.staticCompensation);
    Scalar term2 = Min.of( //
        RealScalar.of(diffRelRckPos).multiply(SteerConfig.GLOBAL.dynamicCompensation), //
        SteerConfig.GLOBAL.dynamicCompensationBoundary);
    return SteerPutEvent.createOn(term1.add(term2));
  }
  // return Optional.of(SteerPutEvent.createOn(Quantity.of(diffRelRckPos > 0 ? 0.3 : -0.3, "SCT")));
  // }
}
