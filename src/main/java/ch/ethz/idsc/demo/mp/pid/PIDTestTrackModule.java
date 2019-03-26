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

/** module requires the GokartTrackReconModule to provide the center line of an
 * identified track */
public class PIDTestTrackModule extends AbstractModule {
  private final PIDController pidController = new PIDController(PIDTuningParams.GLOBAL);
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);

  @Override // from abstractModule
  public void first() {
    Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL.stream().map(Extract2D.FUNCTION));
    pidController.setCurve(Optional.ofNullable(curve));
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(curve);
    pidController.launch();
  }

  @Override // from abstractModule
  public void last() {
    pidController.terminate();
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(null);
  }
}