// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.tensor.Scalar;

public class SteerJoystickModule extends GuideJoystickModule<SteerPutEvent> {
  private final SteerPositionControl steerPositionController = new SteerPositionControl();

  @Override // from AbstractModule
  protected void protected_first() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void protected_last() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<SteerPutEvent> control( //
      SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar desPos = joystick.getSteerLeft().multiply(SteerConfig.GLOBAL.columnMax);
    Scalar difference = desPos.subtract(currAngle);
    Scalar torqueCmd = steerPositionController.iterate(difference);
    return Optional.of(SteerPutEvent.createOn(torqueCmd));
  }
}
