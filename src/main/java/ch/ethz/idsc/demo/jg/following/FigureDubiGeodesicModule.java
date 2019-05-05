// code by jph, gjoel
package ch.ethz.idsc.demo.jg.following;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;
import ch.ethz.idsc.gokart.core.pure.CurvePurePursuitModule;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.gokart.core.pure.PursuitConfig;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;

public class FigureDubiGeodesicModule extends AbstractModule {
  private final CurvePurePursuitModule pursuitModule = new CurveClothoidPursuitModule(PursuitConfig.GLOBAL);

  public FigureDubiGeodesicModule() {
    Tensor curve = DubendorfCurve.TRACK_OVAL_SE2;
    setCurve(curve);
  }

  /** @param curve reference trajectory */
  public void setCurve(Tensor curve) {
    pursuitModule.setCurve(Optional.of(curve));
  }

  @Override // from AbstractModule
  protected final void first() {
    pursuitModule.launch();
  }

  @Override // from AbstractModule
  protected final void last() {
    pursuitModule.terminate();
  }
}
