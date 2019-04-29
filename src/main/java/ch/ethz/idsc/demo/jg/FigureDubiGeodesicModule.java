// code by jph, gjoel
package ch.ethz.idsc.demo.jg;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurveGeodesicPursuitModule;
import ch.ethz.idsc.gokart.core.pure.CurvePurePursuitModule;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.gokart.core.pure.PursuitConfig;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;

public class FigureDubiGeodesicModule extends AbstractModule {
  private final CurvePurePursuitModule pursuitModule = new CurveGeodesicPursuitModule(PursuitConfig.GLOBAL);
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);

  public FigureDubiGeodesicModule() {
    Tensor curve = DubendorfCurve.TRACK_OVAL_SE2;
    setCurve(curve);
  }

  /** @param curve reference trajectory */
  public void setCurve(Tensor curve) {
    pursuitModule.setCurve(Optional.of(curve));
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(Tensor.of(curve.stream().map(Extract2D.FUNCTION)));
  }

  @Override // from AbstractModule
  protected final void first() {
    pursuitModule.launch();
  }

  @Override // from AbstractModule
  protected final void last() {
    pursuitModule.terminate();
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(null);
  }
}
