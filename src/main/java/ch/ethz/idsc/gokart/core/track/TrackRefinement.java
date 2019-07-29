// code by mh
package ch.ethz.idsc.gokart.core.track;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.spline.BSpline2Vector;
import ch.ethz.idsc.sophus.flt.ga.Regularization2Step;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Ramp;

public class TrackRefinement {
  private static final Scalar GD_REGULARIZER = RealScalar.of(0.007);
  private static final TensorUnaryOperator REGULARIZATION_CYCLIC = Regularization2Step.cyclic(RnGeodesic.INSTANCE, GD_REGULARIZER);
  private static final TensorUnaryOperator REGULARIZATION_STRING = Regularization2Step.string(RnGeodesic.INSTANCE, GD_REGULARIZER);
  // ---
  private static final Scalar GD_RADIUS_GROWTH = Quantity.of(0.07, SI.METER);
  private static final Scalar GD_LIMITS = RealScalar.of(1);
  private static final Scalar GD_RADIUS = RealScalar.of(1);
  // ---
  private final RegionRayTrace regionRayTrace;

  public TrackRefinement(Region<Tensor> region) {
    regionRayTrace = new RegionRayTrace( //
        region, //
        Quantity.of(0.1, SI.METER), //
        Quantity.of(1, SI.METER), // probably max center line shift
        Quantity.of(10, SI.METER)); // probably max boundary shift
  }

  Tensor getRefinedTrack(Tensor points_xyr, int resolution, int iterations, boolean cyclic) {
    final int n = points_xyr.length();
    Tensor domain = Tensors.vector(i -> RealScalar.of(i / (double) resolution), (cyclic ? n : n - 2) * resolution);
    Tensor matrixD0 = domain.map(BSpline2Vector.of(n, 0, cyclic));
    Tensor matrixD1 = domain.map(BSpline2Vector.of(n, 1, cyclic));
    Tensor matrixD0Transp = Transpose.of(matrixD0);
    // ---
    int iteration = 0;
    while (iteration < iterations) {
      Optional<Tensor> optional = correction(points_xyr, matrixD0, matrixD1, resolution);
      if (!optional.isPresent()) {
        System.err.println("Iterated " + iteration + " give up");
        return null;
      }
      points_xyr = points_xyr.add(matrixD0Transp.dot(optional.get()));
      points_xyr.set(GD_RADIUS_GROWTH::add, Tensor.ALL, 2);
      points_xyr = cyclic //
          ? REGULARIZATION_CYCLIC.apply(points_xyr)
          : REGULARIZATION_STRING.apply(points_xyr);
      ++iteration;
    }
    System.out.println("Iterate " + iteration + " times");
    return points_xyr;
  }

  private Optional<Tensor> correction(Tensor points_xyr, Tensor matrixD0, Tensor matrixD1, int resolution) {
    return correction( //
        matrixD0.dot(points_xyr), //
        SidewardsUnitVectors.of(matrixD1.dot(Tensor.of(points_xyr.stream().map(Extract2D.FUNCTION)))), //
        resolution);
  }

  /** .
   * @param points_xyr
   * @param matrixD0
   * @param matrixD1
   * @param resolution
   * @return */
  private Optional<Tensor> correction(Tensor dense_xyr, Tensor dense_dir, int resolution) {
    List<Limit> limits = new ArrayList<>();
    for (int index = 0; index < dense_xyr.length(); ++index)
      limits.add(regionRayTrace.getLimits( //
          dense_xyr.get(index).extract(0, 2), //
          dense_dir.get(index)));
    // boolean hasNoSolution = sideLimits.stream().anyMatch(row -> row.get(0).equals(row.get(1)));
    // if (hasNoSolution) {
    // // return Optional.empty();
    // }
    // upwardsforce
    Tensor clipLo = Tensors.vector(i -> Ramp.FUNCTION.apply(dense_xyr.Get(i, 2).add(limits.get(i).lo)), dense_xyr.length());
    Tensor clipHi = Tensors.vector(i -> Ramp.FUNCTION.apply(dense_xyr.Get(i, 2).subtract(limits.get(i).hi)), dense_xyr.length());
    Tensor dirCor = clipLo.subtract(clipHi).multiply(GD_LIMITS.divide(RealScalar.of(resolution)));
    Tensor posCor = dirCor.pmul(dense_dir);
    Tensor radCor = clipHi.add(clipLo).multiply(GD_RADIUS.divide(RealScalar.of(resolution)).negate());
    return Optional.of(Join.of(1, posCor, radCor.map(Tensors::of)));
  }
}
