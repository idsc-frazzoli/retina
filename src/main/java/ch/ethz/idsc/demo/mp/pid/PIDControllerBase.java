// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;

// TODO JPH probably does not need to extend from AbstractClockedModule
/* package */ abstract class PIDControllerBase extends AbstractClockedModule {
  final PIDSteer pidSteer = new PIDSteer();
  final PIDTuningParams pidTuningParams;

  public PIDControllerBase(PIDTuningParams pidTuningParams) {
    this.pidTuningParams = pidTuningParams;
  }

  protected abstract void protected_first();

  protected abstract void protected_last();

  protected abstract Optional<Scalar> deriveHeading();

  @Override // from AbstractClockedModule
  public final void runAlgo() {
    Optional<Scalar> heading = deriveHeading();
    if (heading.isPresent())
      pidSteer.setHeading(heading.get());
  }

  @Override // from AbstractModule
  public final void first() {
    protected_first();
    pidSteer.start();
  }

  @Override // from AbstractModule
  public final void last() {
    pidSteer.stop();
    protected_last();
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return pidTuningParams.updatePeriod;
  }
}
