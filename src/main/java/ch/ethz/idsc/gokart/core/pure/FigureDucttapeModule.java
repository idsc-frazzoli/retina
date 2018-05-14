// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;

public class FigureDucttapeModule extends AbstractModule {
  public static final Tensor CURVE = DubendorfCurve.HYPERLOOP_DUCTTAPE;
  // ---
  private final PurePursuitModule purePursuitModule = new PurePursuitModule();

  @Override // from AbstractModule
  protected void first() throws Exception {
    purePursuitModule.setCurve(Optional.of(CURVE));
    purePursuitModule.launch();
  }

  @Override // from AbstractModule
  protected void last() {
    purePursuitModule.terminate();
  }
}
