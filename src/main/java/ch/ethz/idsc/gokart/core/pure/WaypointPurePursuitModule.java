// code by mg
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class WaypointPurePursuitModule extends PurePursuitModule {
  private Optional<Tensor> lookAhead = Optional.empty();

  // public for testing
  public Optional<Scalar> getRatio() {
    Optional<Tensor> lookAhead = this.lookAhead;
    if (lookAhead.isPresent()) {
      Optional<Scalar> ratio = PurePursuit.ratioPositiveX(lookAhead.get());
      return ratio;
    }
    System.err.println("no valid ratio");
    return Optional.empty();
  }

  // TODO use quantity and meters
  /** @param lookAhead {x, y} in go kart frame coordinates */
  public void setLookAhead(Optional<Tensor> lookAhead) {
    this.lookAhead = lookAhead;
  }

  @Override // from AbstractModule
  protected final void first() throws Exception {
    joystickLcmProvider.startSubscriptions();
    purePursuitRimo.start();
    purePursuitSteer.start();
  }

  @Override // from AbstractModule
  protected final void last() {
    purePursuitRimo.stop();
    purePursuitSteer.stop();
    joystickLcmProvider.stopSubscriptions();
  }

  @Override // from PurePursuitModule
  protected boolean isOperational() {
    Optional<Scalar> ratio = getRatio();
    if (ratio.isPresent()) { // is look ahead beacon available?
      Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(ratio.get());
      if (angleClip.isInside(angle)) { // is look ahead beacon within steering range?
        purePursuitSteer.setHeading(angle);
        Optional<JoystickEvent> joystick = joystickLcmProvider.getJoystick();
        if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
          GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
          return gokartJoystickInterface.isAutonomousPressed();
        }
      } else
        System.err.println("beacon outside steering range");
    }
    return false; // autonomous operation denied
  }
}
