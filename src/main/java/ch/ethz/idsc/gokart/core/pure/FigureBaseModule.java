// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;

public abstract class FigureBaseModule extends AbstractModule {
  private final CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);

  protected FigureBaseModule(Tensor curve) {
    setCurve(curve);
  }

  /** @param curve reference trajectory */
  public void setCurve(Tensor curve) {
    purePursuitModule.setCurve(Optional.of(curve));
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(Tensor.of(curve.stream().map(Extract2D.FUNCTION)));
  }

  @Override // from AbstractModule
  protected final void first() {
    purePursuitModule.launch();
  }

  @Override // from AbstractModule
  protected final void last() {
    purePursuitModule.terminate();
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(null);
  }
}
