// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;
import java.util.function.Predicate;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.PursuitPlanLcm;
import ch.ethz.idsc.owl.math.pursuit.AssistedCurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.CurvePoint;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatio;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class CurveClothoidPursuitPlanner {
  private final ClothoidPursuitConfig clothoidPursuitConfig;
  // ---
  /** previous plan */
  private Optional<ClothoidPlan> plan_prev = Optional.empty();
  private int prevIndex = 0;

  public CurveClothoidPursuitPlanner(ClothoidPursuitConfig clothoidPursuitConfig) {
    this.clothoidPursuitConfig = clothoidPursuitConfig;
  }

  public Optional<ClothoidPlan> getPlan(Tensor pose, Scalar speed, Tensor curve, boolean isForward) {
    return getPlan(pose, speed, curve, true, isForward);
  }

  /** @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @param curve in world coordinates
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @param closed whether curve is closed or not
   * @return geodesic plan */
  public Optional<ClothoidPlan> getPlan(Tensor pose, Scalar speed, Tensor curve, boolean closed, boolean isForward) {
    // TODO GJOEL/JPH can use more general velocity {vx, vy, omega} from state estimation
    Optional<ClothoidPlan> optional = replanning(pose, speed, curve, closed, isForward);
    if (optional.isPresent()) {
      plan_prev = optional;
      PursuitPlanLcm.publish(GokartLcmChannel.PURSUIT_PLAN, pose, Last.of(optional.get().curve()), isForward);
    }
    return optional;
  }

  private Optional<ClothoidPlan> replanning(Tensor pose, Scalar speed, Tensor curve, boolean closed, boolean isForward) {
    TensorUnaryOperator tensorUnaryOperator = new Se2GroupElement(pose).inverse()::combine;
    Tensor tensor = Tensor.of(curve.stream().map(tensorUnaryOperator));
    if (!isForward)
      ClothoidPursuitHelper.mirrorAndReverse(tensor);
    Predicate<Scalar> isCompliant = clothoidPursuitConfig.ratioLimits()::isInside;
    Scalar lookAhead = clothoidPursuitConfig.lookAhead;
    do {
      AssistedCurveIntersection assistedCurveIntersection = clothoidPursuitConfig.getAssistedCurveIntersection();
      Optional<CurvePoint> curvePoint = closed //
          ? assistedCurveIntersection.cyclic(tensor, prevIndex) //
          : assistedCurveIntersection.string(tensor, prevIndex);
      if (curvePoint.isPresent()) {
        Tensor xya = curvePoint.get().getTensor();
        ClothoidTerminalRatio clothoidTerminalRatio = ClothoidTerminalRatios.planar(xya.map(Scalar::zero), xya);
        if (isCompliant.test(clothoidTerminalRatio.head()) && isCompliant.test(clothoidTerminalRatio.tail())) {
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