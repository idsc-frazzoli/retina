// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;

public abstract class PIDControllerModule extends AbstractClockedModule {
  protected final PIDTuningParams tuningParams;
  public final PIDSteer pidSteer = new PIDSteer();

  PIDControllerModule(PIDTuningParams tuningParams) {
    this.tuningParams = tuningParams;
  }

  protected abstract void protected_first();

  protected abstract void protected_last();

  protected abstract Optional<Scalar> deriveHeading();

  @Override // from AbstractClockedModule
  public void runAlgo() {
    Optional<Scalar> heading = deriveHeading();
    if (heading.isPresent())
      pidSteer.setHeading(heading.get());
  }

  @Override // from AbstractModule
  public
  final void first() {
    protected_first();
    pidSteer.start();
  }

  @Override // from AbstractModule
  public
  final void last() {
    pidSteer.stop();
    protected_last();
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return tuningParams.updatePeriod;
  }
}
