// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;
import java.util.function.Predicate;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.PursuitPlanLcm;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.pursuit.AssistedCurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.CurvePoint;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO JPH rename
public class CurveClothoidPursuitPlanner {
  private final ClothoidPursuitConfig clothoidPursuitConfig;
  // ---
  /** previous plan */
  private Optional<ClothoidPlan> plan_prev = Optional.empty();
  private int prevIndex = 0;

  public CurveClothoidPursuitPlanner(ClothoidPursuitConfig clothoidPursuitConfig) {
    this.clothoidPursuitConfig = clothoidPursuitConfig;
  }

  /** @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @param curve in world coordinates
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @param ratioLimits depending on pose and speed
   * @return geodesic plan */
  public Optional<ClothoidPlan> getPlan(Tensor pose, Scalar speed, Tensor curve, boolean isForward) {
    Tensor estimatedPose = pose;
    if (clothoidPursuitConfig.estimatePose && plan_prev.isPresent()) {
      // TODO can use more general velocity {vx, vy, omega} from state estimation
      Flow flow = CarHelper.singleton(speed, plan_prev.get().ratio());
      estimatedPose = Se2CarIntegrator.INSTANCE.step(flow, pose, clothoidPursuitConfig.estimationTime);
    }
    Optional<ClothoidPlan> optional = replanning(estimatedPose, speed, curve, isForward);
    if (optional.isPresent()) {
      plan_prev = optional;
      // TODO GJOEL check if jans "Last.of(optional.get().curve()" is the desired coordinate to publish
      PursuitPlanLcm.publish(GokartLcmChannel.PURSUIT_PLAN, pose, Last.of(optional.get().curve()), isForward);
    }
    // FIXME GJOEL in case of failure, the gokart should not use the old plan indefinitely, or even ever!
    return plan_prev;
  }

  private Optional<ClothoidPlan> replanning(Tensor pose, Scalar speed, Tensor curve, boolean isForward) {
    TensorUnaryOperator tensorUnaryOperator = new Se2GroupElement(pose).inverse()::combine;
    Tensor tensor = Tensor.of(curve.stream().map(tensorUnaryOperator));
    if (!isForward)
      CurveClothoidPursuitHelper.mirrorAndReverse(tensor);
    Predicate<Scalar> isCompliant = //
        CurveClothoidPursuitHelper.isCompliant(clothoidPursuitConfig.ratioLimits(), pose, speed);
    Scalar lookAhead = clothoidPursuitConfig.lookAhead;
    do {
      AssistedCurveIntersection assistedCurveIntersection = clothoidPursuitConfig.getAssistedCurveIntersection();
      Optional<CurvePoint> curvePoint = assistedCurveIntersection.string(tensor, prevIndex);
      if (curvePoint.isPresent()) {
        Tensor xya = curvePoint.get().getTensor();
        ClothoidTerminalRatios clothoidTerminalRatios = ClothoidTerminalRatios.of(xya.map(Scalar::zero), xya);
        if (isCompliant.test(clothoidTerminalRatios.head()) && isCompliant.test(clothoidTerminalRatios.tail())) {
          Optional<ClothoidPlan> optional = ClothoidPlan.from(xya, pose, isForward);
          if (optional.isPresent()) {
            prevIndex = curvePoint.get().getIndex();
            return optional;
          }
        }
      }
      lookAhead = lookAhead.add(clothoidPursuitConfig.lookAheadResolution);
    } while (Scalars.lessEquals(lookAhead, clothoidPursuitConfig.fallbackLookAhead));
    return Optional.empty();
  }
}