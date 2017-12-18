// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

class PurePursuitSteer extends PurePursuitBase implements SteerPutProvider {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
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
  public void setHeading(Scalar angle) {
    this.angle = angle;
  }

  /***************************************************/
  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (isOperational())
      return control(steerColumnInterface);
    return Optional.empty();
  }

  Optional<SteerPutEvent> control(SteerColumnInterface steerColumnInterface) {
    if (steerColumnInterface.isSteerColumnCalibrated()) {
      Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
      Scalar desPos = SteerConfig.GLOBAL.getSCEfromAngle(angle);
      Scalar difference = desPos.subtract(currAngle);
      Scalar torqueCmd = steerPositionController.iterate(difference);
      return Optional.of(SteerPutEvent.createOn(torqueCmd));
    }
    return Optional.empty();
  }

  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
