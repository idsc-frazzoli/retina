// code by mcp (used CenterLinePursuiteModule by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;

public class PIDTestTrackModule extends AbstractModule {
  private final PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);

  @Override // from AbstractModule
  public void first() {
    Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL.stream().map(Extract2D.FUNCTION));
    pidControllerModule.setCurve(Optional.ofNullable(curve));
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(curve);
    pidControllerModule.launch();
  }

  @Override // from AbstractModule
  public void last() {
    pidControllerModule.terminate();
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(null);
  }
}