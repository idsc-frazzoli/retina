//code by mh
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class SteeringStepTestSteering implements SteerPutProvider {
  private boolean isActive = false;
  private final SteerPositionControl steerPositionController = new SteerPositionControl();
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private Scalar scalar;
  // private final Timing timing = Timing.started();
  private Boolean turnOff = false;

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (isActive) {
      Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
      Scalar steering = RealScalar.ZERO;
      Scalar difference = steering.subtract(currAngle);
      Scalar torqueCmd = steerPositionController.iterate(difference);
      return Optional.of(SteerPutEvent.createOn(torqueCmd));
    }
    return null;
  }

  public void startStep(Scalar scalar) {
    this.scalar = scalar;
    isActive = true;
  }

  public void stopPress() {
    isActive = false;
  }
}
