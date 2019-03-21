// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PowerSteeringModule extends AbstractModule implements SteerGetListener, SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final LidarLocalizationModule localisation = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();

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

  // SteerGetEvent prev;
  // double diffRelRckPos;
  @Override
  public void getEvent(SteerGetEvent getEvent) {
    // if (prev != null) {
    // diffRelRckPos = getEvent.getGcpRelRckPos() - prev.getGcpRelRckPos();
    // }
    // prev = getEvent;
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      Scalar currangle = steerColumnTracker.getSteerColumnEncoderCentered();
      // Tensor velocity = localisation.getVelocity();
      // Scalar angularvelocity = localisation.getGyroZFiltered();
      return Optional.of(SteerPutEvent.createOn(currangle.multiply(SteerConfig.GLOBAL.staticCompensation)));
    }
    return Optional.empty();
  }
  // return Optional.of(SteerPutEvent.createOn(Quantity.of(diffRelRckPos > 0 ? 0.3 : -0.3, "SCT")));
  // }
}
