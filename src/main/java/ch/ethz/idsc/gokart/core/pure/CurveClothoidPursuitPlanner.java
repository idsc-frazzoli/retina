// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;

public class CurveClothoidPursuitPlanner {
  /* package for testing */ static final Comparator<Tensor> COMPARATOR = new Comparator<Tensor>() {
    Chop chop = Chop.below(.1); // TODO find value

    @Override
    public int compare(Tensor o1, Tensor o2) {
      if (chop.close(o1.Get(0), o2.Get(0)))
        return Scalars.lessThan(o1.Get(1), o2.Get(1))? 1 : -1;
      else
        return Scalars.lessThan(o1.Get(0), o2.Get(0))? -1 : 1;
    }
  };
  // ---
  private final ClothoidPursuitConfig clothoidPursuitConfig;
  private final Map<Scalar, Integer> prevIndices = new HashMap<>();
  private final List<Tensor> calc = new ArrayList<>();

  public CurveClothoidPursuitPlanner(ClothoidPursuitConfig clothoidPursuitConfig) {
    this.clothoidPursuitConfig = clothoidPursuitConfig;
  }

  public Optional<ClothoidPlan> getPlan(Tensor pose, Tensor speed, Tensor curve, boolean isForward) {
    return getPlan(pose, speed, curve, true, isForward);
  }

  /** @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle {vx[m*s^-1], vy[m*s^-1], gyroZ[s^-1]}
   * @param curve in world coordinates
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @param closed whether curve is closed or not
   * @return geodesic plan */
  public Optional<ClothoidPlan> getPlan(Tensor pose, Tensor speed, Tensor curve, boolean closed, boolean isForward) {
    Optional<ClothoidPlan> optional = replanning(pose, speed, curve, closed, isForward);
    if (optional.isPresent())
      PursuitPlanLcm.publish(GokartLcmChannel.PURSUIT_PLAN, pose, Last.of(optional.get().curve()), isForward);
    return optional;
  }

  private Optional<ClothoidPlan> replanning(Tensor pose, Tensor speed, Tensor curve, boolean closed, boolean isForward) {
    TensorUnaryOperator tensorUnaryOperator = new Se2GroupElement(pose).inverse()::combine;
    Tensor tensor = Tensor.of(curve.stream().map(tensorUnaryOperator));
    if (!isForward)
      ClothoidPursuitHelper.mirrorAndReverse(tensor);
    calc.clear();
    clothoidPursuitConfig.lookAheads().parallel().forEach(distance -> {
      AssistedCurveIntersection assistedCurveIntersection = clothoidPursuitConfig.getAssistedCurveIntersection();
      Optional<CurvePoint> goal = closed //
          ? assistedCurveIntersection.cyclic(tensor, prevIndices.getOrDefault(distance, 0)) //
          : assistedCurveIntersection.string(tensor, prevIndices.getOrDefault(distance, 0));
      if (goal.isPresent()) {
        Optional<ClothoidPlan> potential = ClothoidPlan.from(goal.get().getTensor(), pose, isForward);
        if (potential.isPresent()) {
          Scalar err = ClothoidPursuitHelper.error(potential.get().curve(), curve);
          ClothoidTerminalRatio ratios = ClothoidTerminalRatios.of(Array.zeros(3), goal.get().getTensor());
          update(distance, goal.get(), err, //
              clothoidPursuitConfig.ratioLimits().isInside(ratios.head()) && clothoidPursuitConfig.ratioLimits().isInside(ratios.tail()));
        }
      }
    });
    Optional<Tensor> lookAhead = Optional.empty();
    if (calc.size() > 0) {
      if (calc.size() > 1)
        calc.sort(COMPARATOR);
      lookAhead = Optional.of(calc.get(0).get(2));
    }
    Optional<ClothoidPlan> plan = lookAhead.map(goal -> ClothoidPlan.from(goal, pose, isForward).orElse(null));
    if (plan.isPresent())
      PursuitPlanLcm.publish(GokartLcmChannel.PURSUIT_PLAN, pose, lookAhead.get(), isForward);
    return plan;
  }

  /** update lookAhead if better
   * @param distance in [m]
   * @param point on curve
   * @param error in [m]
   * @param isCompliant */
  private synchronized void update(Scalar distance, CurvePoint point, Scalar error, boolean isCompliant) {
    prevIndices.put(distance, point.getIndex());
    if (isCompliant)
      calc.add(Tensors.of(error, distance, point.getTensor()));
  }
}