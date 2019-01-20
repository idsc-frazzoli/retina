// code by jph, mg
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Clip;

public abstract class PurePursuitModule extends AbstractClockedModule {
  private final ManualControlProvider joystickLcmProvider = ManualConfig.GLOBAL.createProvider();
  final PurePursuitRimo purePursuitRimo = new PurePursuitRimo();
  final PurePursuitSteer purePursuitSteer = new PurePursuitSteer();
  protected final Clip angleClip = SteerConfig.GLOBAL.getAngleLimit();
  protected final PursuitConfig pursuitConfig;

  PurePursuitModule(PursuitConfig pursuitConfig) {
    this.pursuitConfig = pursuitConfig;
  }

  @Override // from AbstractModule
  protected final void first() throws Exception {
    protected_first();
    joystickLcmProvider.start();
    purePursuitRimo.start();
    purePursuitSteer.start();
  }

  @Override // from AbstractModule
  protected final void last() {
    purePursuitRimo.stop();
    purePursuitSteer.stop();
    joystickLcmProvider.stop();
    protected_last();
  }

  protected abstract void protected_first() throws Exception;

  protected abstract void protected_last();

  /***************************************************/
  @Override // from AbstractClockedModule
  protected final void runAlgo() {
    final Optional<ManualControlInterface> optional = joystickLcmProvider.getManualControl();
    Optional<Scalar> heading = deriveHeading();
    if (heading.isPresent())
      purePursuitSteer.setHeading(heading.get());
    // ---
    final boolean status = optional.isPresent() && heading.isPresent();
    purePursuitSteer.setOperational(status);
    if (status) {
      ManualControlInterface manualControlInterface = optional.get();
      // ante 20180604: the ahead average was used in combination with Ramp
      Scalar ratio = manualControlInterface.getAheadAverage(); // in [-1, 1]
      // post 20180604: the forward command is provided by right slider
      Scalar pair = Differences.of(manualControlInterface.getAheadPair_Unit()).Get(0); // in [0, 1]
      // post 20180619: allow reverse driving
      Scalar speed = Clip.absoluteOne().apply(ratio.add(pair));
      purePursuitRimo.setSpeed(Times.of(pursuitConfig.rateFollower, speed, getSpeedMultiplier()));
    }
    purePursuitRimo.setOperational(status);
  }

  @Override // from AbstractClockedModule
  protected final Scalar getPeriod() {
    return pursuitConfig.updatePeriod;
  }

  /***************************************************/
  /** @return unitless value in the interval [0, 1] */
  protected Scalar getSpeedMultiplier() {
    return DoubleScalar.of(1.0);
  }

  /** @return heading with unit "rad"
   * Optional.empty() if autonomous pure pursuit control is not warranted */
  protected abstract Optional<Scalar> deriveHeading();
}
