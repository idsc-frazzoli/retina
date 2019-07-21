// code by jph, mg
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;

public abstract class PursuitModule extends AbstractClockedModule {
  public final PursuitSteer pursuitSteer = new PursuitSteer();
  protected final PursuitConfig pursuitConfig;

  protected PursuitModule(PursuitConfig pursuitConfig) {
    this.pursuitConfig = pursuitConfig;
  }

  @Override // from AbstractModule
  protected final void first() {
    protected_first();
    pursuitSteer.start();
  }

  @Override // from AbstractModule
  protected final void last() {
    pursuitSteer.stop();
    protected_last();
  }

  protected abstract void protected_first();

  protected abstract void protected_last();

  /***************************************************/
  @Override // from AbstractClockedModule
  public final void runAlgo() {
    Optional<Scalar> heading = deriveHeading();
    if (heading.isPresent())
      pursuitSteer.setRatio(heading.get());
    pursuitSteer.setOperational(heading.isPresent());
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

  /** @return heading unitless with interpretation in radian
   * Optional.empty() if autonomous pure pursuit control is not warranted */
  protected abstract Optional<Scalar> deriveHeading();
}
