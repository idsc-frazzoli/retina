// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.PursuitPlanLcm;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import ch.ethz.idsc.owl.car.math.SphereSe2CurveIntersection;
import ch.ethz.idsc.owl.math.planar.ClothoidTerminalRatios;
import ch.ethz.idsc.owl.math.planar.CurvePoint;
import ch.ethz.idsc.owl.math.planar.PseudoSe2CurveIntersection;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO JPH rename
public class CurveClothoidPursuitPlanner {
  private final Map<Scalar, Integer> prevIndices = new HashMap<>();
  private Optional<ClothoidPlan> plan = Optional.empty();
  private Optional<Tensor> lookAhead;
  private Scalar error;

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
    lookAhead = Optional.empty();
    error = Quantity.of(DoubleScalar.POSITIVE_INFINITY , SI.METER);
    config.lookAheads().parallel().forEach(distance -> {
      Optional<CurvePoint> goal = (config.se2distance //
          ? new PseudoSe2CurveIntersection(distance) //
          : new SphereSe2CurveIntersection(distance)).string(tensor, prevIndices.getOrDefault(distance, 0));
      if (goal.isPresent()) {
        Optional<ClothoidPlan> potential = ClothoidPlan.from(goal.get().getTensor(), pose, isForward);
        if (potential.isPresent()) {
          Scalar err = CurveClothoidPursuitHelper.error(potential.get().curve(), curve);
          ClothoidTerminalRatios ratios = ClothoidTerminalRatios.of(Array.zeros(3), goal.get().getTensor());
          update(distance, goal.get(), err, //
              isCompliant.test(ratios.head()) && isCompliant.test(ratios.tail()));
        }
      }
    });
    plan = lookAhead.map(goal -> ClothoidPlan.from(goal, pose, isForward).orElse(null));
    if (plan.isPresent())
      PursuitPlanLcm.publish(GokartLcmChannel.PURSUIT_PLAN, pose, lookAhead.get(), isForward);
  }

  /** update lookAhead if better
   * @param distance in [m]
   * @param point on curve
   * @param error in [m]
   * @param isCompliant */
  private synchronized void update(Scalar distance, CurvePoint point, Scalar error, boolean isCompliant) {
    prevIndices.put(distance, point.getIndex());
    if (isCompliant && Scalars.lessThan(error, this.error)) {
      this.error = error;
      lookAhead = Optional.of(point.getTensor());
    }
  }
}