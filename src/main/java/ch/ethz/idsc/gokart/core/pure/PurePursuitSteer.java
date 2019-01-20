// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

final class PurePursuitSteer extends PurePursuitBase<SteerPutEvent> {
  private static final Optional<SteerPutEvent> FALLBACK = Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_1);
  // ---
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final SteerPositionControl steerPositionController = new SteerPositionControl();

  @Override // from StartAndStoppable
  public void start() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from StartAndStoppable
  public void stop() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  private Scalar angle = Quantity.of(0.0, SIDerived.RADIAN);

  /** @param angle with unit "rad" */
  /* package */ void setHeading(Scalar angle) {
    this.angle = angle;
  }

  /* package */ Scalar getHeading() {
    return angle;
  }

  /***************************************************/
  @Override // from PurePursuitBase
  Optional<SteerPutEvent> control(SteerColumnInterface steerColumnInterface) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar desPos = steerMapping.getSCEfromAngle(angle);
    Scalar difference = desPos.subtract(currAngle);
    Scalar torqueCmd = steerPositionController.iterate(difference);
    return Optional.of(SteerPutEvent.createOn(torqueCmd));
  }

  @Override // from PurePursuitBase
  Optional<SteerPutEvent> fallback() {
    return FALLBACK;
  }
}
