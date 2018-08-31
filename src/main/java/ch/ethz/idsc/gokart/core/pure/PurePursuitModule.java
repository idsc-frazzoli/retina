// code by jph, mg
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.sca.Clip;

public abstract class PurePursuitModule extends AbstractClockedModule {
  protected final Clip angleClip = SteerConfig.GLOBAL.getAngleLimit();
  final PurePursuitSteer purePursuitSteer = new PurePursuitSteer();
  final PurePursuitRimo purePursuitRimo = new PurePursuitRimo();
  protected final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();

  @Override // from AbstractClockedModule
  protected final void runAlgo() {
    boolean status = isOperational();
    purePursuitSteer.setOperational(status);
    Optional<JoystickEvent> joystick = joystickLcmProvider.getJoystick();
    if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
      GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
      // ante 20180604: the ahead average was used in combination with Ramp
      Scalar ratio = gokartJoystickInterface.getAheadAverage(); // in [-1, 1]
      // post 20180604: the forward command is provided by right slider
      Scalar pair = Differences.of(gokartJoystickInterface.getAheadPair_Unit()).Get(0); // in [0, 1]
      // post 20180619: allow reverse driving
      Scalar speed = Clip.absoluteOne().apply(ratio.add(pair));
      purePursuitRimo.setSpeed(PursuitConfig.GLOBAL.rateFollower.multiply(speed));
    }
    purePursuitRimo.setOperational(status);
  }

  @Override // from AbstractClockedModule
  protected final Scalar getPeriod() {
    return PursuitConfig.GLOBAL.updatePeriod;
  }

  abstract protected boolean isOperational();
}
