// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;

/* package */ abstract class PIDControllerBase extends AbstractClockedModule {
  final PIDSteer pidSteer = new PIDSteer();
  private final PIDTuningParams pidTuningParams;

  public PIDControllerBase(PIDTuningParams pidTuningParams) {
    this.pidTuningParams = pidTuningParams;
  }

  @Override // from AbstractClockedModule
  public final void runAlgo() {
    Optional<Scalar> ratio = deriveRatio();
    if (ratio.isPresent())
      pidSteer.setRatio(ratio.get());
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

  protected abstract void protected_first();

  protected abstract void protected_last();

  /** @return */
  protected abstract Optional<Scalar> deriveRatio();
}
