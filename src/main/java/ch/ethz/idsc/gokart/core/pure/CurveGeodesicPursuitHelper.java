// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import ch.ethz.idsc.owl.math.planar.ArgMinVariable;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.owl.math.planar.GeodesicPursuit;
import ch.ethz.idsc.owl.math.planar.GeodesicPursuitInterface;
import ch.ethz.idsc.owl.math.planar.TrajectoryEntryFinder;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Abs;

public enum CurveGeodesicPursuitHelper {
  ;
  /** @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @param curve in world coordinates
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @param geodesicInterface type of planned curve
   * @param trajectoryEntryFinder strategy to find best re-entry point
   * @param ratioLimits depending on pose and speed
   * @return geodesic plan */
  static Optional<GeodesicPlan> getPlan( //
      Tensor pose, Scalar speed, Tensor curve, boolean isForward, //
      GeodesicInterface geodesicInterface, //
      TrajectoryEntryFinder trajectoryEntryFinder, //
      List<DynamicRatioLimit> ratioLimits) {
    TensorUnaryOperator tensorUnaryOperator = new Se2GroupElement(pose).inverse()::combine;
    Tensor tensor = Tensor.of(curve.stream().map(tensorUnaryOperator));
    if (!isForward)
      mirrorAndReverse(tensor);
    Predicate<Scalar> isCompliant = isCompliant(ratioLimits, pose, speed);
    TensorScalarFunction mapping = vector -> dragonNightKingKnife(vector, geodesicInterface, isCompliant, speed);
    Scalar var = ArgMinVariable.using(trajectoryEntryFinder, mapping, GeodesicPursuitParams.GLOBAL.getOptimizationSteps()).apply(tensor);
    Optional<Tensor> lookAhead = trajectoryEntryFinder.on(tensor).apply(var).point;
    return lookAhead.map(vector -> GeodesicPlan.from(new GeodesicPursuit(geodesicInterface, vector), pose, isForward).orElse(null));
  }

  /** @param vector
   * @param geodesicInterface
   * @param isCompliant
   * @param speed
   * @return quantity with unit [m] */
  public static Scalar dragonNightKingKnife(Tensor vector, GeodesicInterface geodesicInterface, Predicate<Scalar> isCompliant, Scalar speed) {
    if (Scalars.lessThan(GeodesicPursuitParams.GLOBAL.minDistance, Norm._2.ofVector(Extract2D.FUNCTION.apply(vector)))) {
      GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(geodesicInterface, vector);
      Tensor ratios = geodesicPursuit.ratios();
      if (ratios.stream().map(Tensor::Get).allMatch(isCompliant)) {
        Scalar length = curveLength(geodesicPursuit.curve()); // [m]
        // System.out.println("length=" + length);
        Scalar max = Abs.of(geodesicPursuit.ratios().stream().reduce(Max::of).get()).Get(); // [m^-1]
        // System.out.println("max=" + max);
        Scalar virtual = Times.of(GeodesicPursuitParams.GLOBAL.scale, speed, max);
        // System.out.println("virtual=" + virtual);
        return length.add(virtual);
      }
    }
    return Quantity.of(DoubleScalar.POSITIVE_INFINITY, SI.METER);
  }

  /** mirror the points along the y axis and invert their orientation
   * @param se2points curve given by points {x,y,a} */
  /* package */ static void mirrorAndReverse(Tensor se2points) {
    se2points.set(Scalar::negate, Tensor.ALL, 0);
    se2points.set(Scalar::negate, Tensor.ALL, 2);
  }

  /** @param ratioLimits depending on pose and speed
   * @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @return predicate to determine whether ratio is compliant with all posed turning ratio limits */
  private static Predicate<Scalar> isCompliant(List<DynamicRatioLimit> ratioLimits, Tensor pose, Scalar speed) {
    return ratio -> ratioLimits.stream() //
        .map(dynamicRatioLimit -> dynamicRatioLimit.at(pose, speed)) //
        .allMatch(dynamicRatioLimit -> dynamicRatioLimit.isInside(ratio));
  }

  /** @param curve geodesic
   * @return approximated length of curve */
  private static Scalar curveLength(Tensor curve) {
    Tensor curve_ = Tensor.of(curve.stream().map(Extract2D.FUNCTION));
    int n = curve_.length();
    return curve_.extract(1, n).subtract(curve_.extract(0, n - 1)).stream() //
        .map(Norm._2::ofVector) //
        .reduce(Scalar::add).get();
  }
}

/* package */ class GeodesicPlan {
  public final Scalar ratio;
  public final Tensor curve;

  /** @param ratio [m^-1] used to derive future heading
   * @param curve planned to be followed */
  public GeodesicPlan(Scalar ratio, Tensor curve) {
    this.ratio = ratio;
    this.curve = curve;
  }

  /** @param geodesicPursuitInterface
   * @param pose of vehicle {x[m], y[m], angle}
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @return GeodesicPlan */
  public static Optional<GeodesicPlan> from(GeodesicPursuitInterface geodesicPursuitInterface, Tensor pose, boolean isForward) {
    Optional<Scalar> optional = geodesicPursuitInterface.firstRatio(); // with unit [m^-1]
    // System.out.println("optional=" + optional);
    // return optional
    if (optional.isPresent()) {
      Scalar ratio = optional.get();
      Tensor curveSE2 = geodesicPursuitInterface.curve();
      if (!isForward)
        CurveGeodesicPursuitHelper.mirrorAndReverse(curveSE2);
      Tensor curve = Tensor.of(curveSE2.stream().map(new Se2GroupElement(pose)::combine));
      return Optional.of(new GeodesicPlan(ratio, curve));
    }
    return Optional.empty();
  }
}