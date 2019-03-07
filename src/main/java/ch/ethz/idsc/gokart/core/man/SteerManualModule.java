// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;

public class SteerManualModule extends GuideManualModule<SteerPutEvent> {
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
      SteerColumnInterface steerColumnInterface, ManualControlInterface manualControlInterface) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar desPos = manualControlInterface.getSteerLeft().multiply(SteerConfig.GLOBAL.columnMax);
    Scalar difference = desPos.subtract(currAngle);
    Scalar torqueCmd = steerPositionController.iterate(difference);
    return Optional.of(SteerPutEvent.createOn(torqueCmd));
  }
}
