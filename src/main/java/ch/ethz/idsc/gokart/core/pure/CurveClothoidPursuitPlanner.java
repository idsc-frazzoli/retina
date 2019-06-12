// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.PursuitPlanLcm;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import ch.ethz.idsc.owl.math.pursuit.CurvePoint;
import ch.ethz.idsc.owl.math.pursuit.PseudoSe2CurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.SphereSe2CurveIntersection;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO JPH rename
public class CurveClothoidPursuitPlanner {
  private Optional<ClothoidPlan> plan = Optional.empty();
  private int prevIndex = 0;

  /** @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @param curve in world coordinates
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @param ratioLimits depending on pose and speed
   * @return geodesic plan */
  public Optional<ClothoidPlan> getPlan( //
      Tensor pose, Scalar speed, Tensor curve, boolean isForward, //
      List<DynamicRatioLimit> ratioLimits) {
    ClothoidPursuitConfig config = ClothoidPursuitConfig.GLOBAL;
    Tensor estimatedPose = config.estimatePose && plan.isPresent() //
        ? Se2CarIntegrator.INSTANCE.step(CarHelper.singleton(speed, plan.get().ratio()), pose, config.estimationTime) //
        : pose;
    replanning(estimatedPose, speed, curve, isForward, ratioLimits);
    return plan;
  }

  private void replanning( //
      Tensor pose, Scalar speed, Tensor curve, boolean isForward, //
      List<DynamicRatioLimit> ratioLimits) {
    ClothoidPursuitConfig config = ClothoidPursuitConfig.GLOBAL;
    TensorUnaryOperator tensorUnaryOperator = new Se2GroupElement(pose).inverse()::combine;
    Tensor tensor = Tensor.of(curve.stream().map(tensorUnaryOperator));
    if (!isForward)
      CurveClothoidPursuitHelper.mirrorAndReverse(tensor);
    Predicate<Scalar> isCompliant = CurveClothoidPursuitHelper.isCompliant(ratioLimits, pose, speed);
    plan = Optional.empty();
    Scalar dist = config.lookAhead;
    do {
      Optional<CurvePoint> lookAhead = (config.se2distance //
          ? new PseudoSe2CurveIntersection(dist) //
          : new SphereSe2CurveIntersection(dist)).string(tensor, prevIndex);
      if (lookAhead.isPresent()) {
        ClothoidTerminalRatios ratios = ClothoidTerminalRatios.of(Array.zeros(3), lookAhead.get().getTensor());
        if (isCompliant.test(ratios.head()) && isCompliant.test(ratios.tail())) {
          plan = ClothoidPlan.from(lookAhead.get().getTensor(), pose, isForward);
          if (plan.isPresent()) {
            PursuitPlanLcm.publish(GokartLcmChannel.PURSUIT_PLAN, pose, lookAhead.get().getTensor(), isForward);
            prevIndex = lookAhead.get().getIndex();
            break;
          }
        }
      }
      dist = dist.add(config.lookAheadResolution);
    } while (Scalars.lessEquals(dist, config.fallbackLookAhead));
  }
}