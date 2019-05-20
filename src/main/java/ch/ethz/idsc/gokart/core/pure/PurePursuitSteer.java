// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerFeedForward;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO JPH there is nothing PurePursuit-specific in this class -> rename
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

  private Scalar ratio = Quantity.of(0.0, SI.PER_METER);

  /** @param ratio of turning with unit [m^-1] */
  /* package */ void setRatio(Scalar ratio) {
    this.ratio = ratio;
  }

  // function testing
  /* package */ Scalar getRatio() {
    return ratio;
  }

  /***************************************************/
  @Override // from PurePursuitBase
  Optional<SteerPutEvent> control(SteerColumnInterface steerColumnInterface) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar desPos = steerMapping.getSCEfromRatio(ratio);
    Scalar difference = desPos.subtract(currAngle);
    Scalar torqueCmd = steerPositionController.iterate(difference);
    Scalar feedForward = SteerFeedForward.FUNCTION.apply(currAngle);
    return Optional.of(SteerPutEvent.createOn(torqueCmd.add(feedForward)));
  }

  @Override // from PurePursuitBase
  Optional<SteerPutEvent> fallback() {
    return FALLBACK;
  }
}
