// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class SteerJoystickModule extends AbstractModule implements SteerPutProvider {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerPositionControl positionController = new SteerPositionControl();

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmClient.startSubscriptions();
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(this);
    joystickLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    Optional<JoystickEvent> optional = joystickLcmClient.getJoystick();
    if (optional.isPresent() && steerColumnInterface.isSteerColumnCalibrated()) {
      GokartJoystickInterface joystick = (GokartJoystickInterface) optional.get();
      final Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
      Scalar desPos = RealScalar.of(joystick.getSteerLeft()).multiply(SteerColumnTracker.MAX_SCE);
      final Scalar torqueCmd = //
          positionController.iterate(desPos.subtract(currAngle));
      return Optional.of(SteerPutEvent.createOn(torqueCmd));
    }
    return Optional.empty();
  }
}
