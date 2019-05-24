// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** class is the default choice for geodesic pursuit when driving along a curve in global
 * coordinates while the pose is updated periodically from a localization method. */
public class CurveClothoidPursuitModule extends CurvePursuitModule {
  private final GlobalViewLcmModule globalViewLcmModule = //
      ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);
  private final CurveClothoidPursuitPlanner planner = new CurveClothoidPursuitPlanner();

  public CurveClothoidPursuitModule(PursuitConfig pursuitConfig) {
    super(pursuitConfig);
  }

  @Override // from CurvePurePursuitModule
  protected synchronized Optional<Scalar> getRatio(Tensor pose) {
    Optional<Tensor> optionalCurve = this.optionalCurve; // copy reference instead of synchronize
    if (optionalCurve.isPresent()) {
      Optional<ClothoidPlan> plan = planner.getPlan( //
          pose, //
          speed, //
          optionalCurve.get(), //
          isForward, //
          pursuitConfig.trajectoryEntryFinder, //
          PursuitConfig.ratioLimits());
      if (Objects.nonNull(globalViewLcmModule))
        globalViewLcmModule.setPlan(plan.map(ClothoidPlan::curve).orElse(null));
      return plan.map(ClothoidPlan::ratio);
    }
    System.err.println("no curve in geodesic pursuit");
    return Optional.empty();
  }

  @Override // from PursuitModule
  protected final void protected_last() {
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setPlan(null);
    super.protected_last();
  }
}
