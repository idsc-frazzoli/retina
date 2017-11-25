// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class SteerJoystickModule extends AbstractModule implements SteerPutProvider {
  private final SteerPositionControl positionController = new SteerPositionControl();

  @Override
  protected void first() throws Exception {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    Optional<GokartJoystickInterface> optional = null; // getJoystick(); // FIXME
    if (optional.isPresent()) {
      final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
      if (steerColumnTracker.isCalibrated()) {
        final Scalar currAngle = steerColumnTracker.getEncoderValueCentered();
        Scalar desPos = RealScalar.of(optional.get().getSteerLeft()).multiply(SteerColumnTracker.MAX_SCE);
        final Scalar torqueCmd = //
            positionController.iterate(Quantity.of(desPos.subtract(currAngle), SteerPutEvent.UNIT_ENCODER));
        return Optional.of(SteerPutEvent.createOn(torqueCmd));
      }
    }
    return Optional.empty();
  }
}
