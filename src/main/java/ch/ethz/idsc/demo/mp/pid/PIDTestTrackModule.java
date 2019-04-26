// code by mcp (used CenterLinePursuiteModule by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;

public class PIDTestTrackModule extends AbstractModule {
  private final PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);

  @Override // from AbstractModule
  public void first() {
    Tensor curve = DubendorfCurve.TRACK_OVAL_R2;
    pidControllerModule.setCurve(Optional.of(curve));
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