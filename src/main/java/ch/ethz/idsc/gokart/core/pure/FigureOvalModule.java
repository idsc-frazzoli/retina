// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;

public class FigureOvalModule extends AbstractModule {
  /** until 20180226 the curve for trajectory pursuit was
   * DubendorfCurve.OVAL
   * 
   * due to new safety structure, the curve made a bit smaller and shifted slightly
   * in the direction away from the container. the new curve is
   * DubendorfCurve.OVAL_SHIFTED
   * 
   * then the hyperloop project was introduced to the hanger which further reduced
   * the operating domain for the gokart. */
  public static final Tensor CURVE = DubendorfCurve.EIGHT_HYPERLOOP; // TODO
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
