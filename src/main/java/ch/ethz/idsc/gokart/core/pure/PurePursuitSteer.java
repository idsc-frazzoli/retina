// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

class PurePursuitSteer extends PurePursuitBase<SteerPutEvent> {
  private final SteerPositionControl steerPositionController = new SteerPositionControl();

  @Override // from StartAndStoppable
  public void start() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from StartAndStoppable
  public void stop() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  private Scalar angle = Quantity.of(0.0, "rad");

  /** @param angle with unit "rad" */
  /* package */ void setHeading(Scalar angle) {
    this.angle = angle;
  }

  /* package */ Scalar getHeading() {
    return angle;
  }

  /***************************************************/
  @Override
  Optional<SteerPutEvent> control(SteerColumnInterface steerColumnInterface) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar desPos = SteerConfig.GLOBAL.getSCEfromAngle(angle);
    Scalar difference = desPos.subtract(currAngle);
    Scalar torqueCmd = steerPositionController.iterate(difference);
    return Optional.of(SteerPutEvent.createOn(torqueCmd));
  }
}
