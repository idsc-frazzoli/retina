// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;

abstract class FigureBaseModule extends AbstractModule {
  private final CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);

  protected FigureBaseModule(Tensor curve) {
    purePursuitModule.setCurve(Optional.of(curve));
  }

  @Override // from AbstractModule
  protected final void first() throws Exception {
    purePursuitModule.launch();
  }

  @Override // from AbstractModule
  protected final void last() {
    purePursuitModule.terminate();
  }
}
