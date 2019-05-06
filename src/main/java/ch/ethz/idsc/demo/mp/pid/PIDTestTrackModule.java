// code by mcp (used CenterLinePursuiteModule by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;

public class PIDTestTrackModule extends AbstractModule {
  private final PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);

  @Override // from AbstractModule
  public void first() {
    // TODO MCP use TRACK_OVAL_SE2_UNITS
    Tensor curve = DubendorfCurve.TRACK_OVAL_R2;
    pidControllerModule.setCurve(Optional.of(curve));
    pidControllerModule.launch();
  }

  @Override // from AbstractModule
  public void last() {
    pidControllerModule.terminate();
  }
}