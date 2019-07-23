// code by mh
package ch.ethz.idsc.gokart.core.track;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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

  Tensor getRefinedTrack(Tensor points_xyr, int resolution, int iterations, boolean closed) {
    final int n = points_xyr.length();
    Tensor domain = Tensors.vector(i -> RealScalar.of(i / (double) resolution), (closed ? n : n - 2) * resolution);
    Tensor matrixD0 = domain.map(BSpline2Vector.of(n, 0, closed));
    Tensor matrixD1 = domain.map(BSpline2Vector.of(n, 1, closed));
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
      points_xyr = closed //
          ? REGULARIZATION_CYCLIC.apply(points_xyr)
          : REGULARIZATION_STRING.apply(points_xyr);
      ++iteration;
    }
    System.out.println("Iterate " + iteration + " times");
    return points_xyr;
  }

  /** .
   * @param points_xyr
   * @param matrixD0
   * @param matrixD1
   * @param resolution
   * @return */
  private Optional<Tensor> correction(Tensor points_xyr, Tensor matrixD0, Tensor matrixD1, int resolution) {
    Tensor dense_xyr = matrixD0.dot(points_xyr);
    Tensor dense_dir = SidewardsUnitVectors.of(matrixD1.dot(Tensor.of(points_xyr.stream().map(Extract2D.FUNCTION))));
    List<Limit> sideLimits = new ArrayList<>();
    for (int index = 0; index < dense_xyr.length(); ++index) {
      Tensor pos_xyr = dense_xyr.get(index).extract(0, 2);
      Tensor sidevec = dense_dir.get(index);
      sideLimits.add(regionRayTrace.getLimits(pos_xyr, sidevec));
    }
    // boolean hasNoSolution = sideLimits.stream().anyMatch(row -> row.get(0).equals(row.get(1)));
    // if (hasNoSolution) {
    // // return Optional.empty();
    // }
    // upwardsforce
    Tensor clippingLo = Tensors.vector(i -> Ramp.FUNCTION.apply(dense_xyr.Get(i, 2).add(sideLimits.get(i).lo)), dense_xyr.length());
    Tensor clippingHi = Tensors.vector(i -> Ramp.FUNCTION.apply(dense_xyr.Get(i, 2).subtract(sideLimits.get(i).hi)), dense_xyr.length());
    Tensor sideCorr = clippingLo.subtract(clippingHi).multiply(GD_LIMITS.divide(RealScalar.of(resolution)));
    Tensor posCorr = sideCorr.pmul(dense_dir);
    Tensor radiusCorr = clippingHi.add(clippingLo).multiply(GD_RADIUS.divide(RealScalar.of(resolution)).negate());
    return Optional.of(Tensor.of(IntStream.range(0, posCorr.length()).mapToObj(i -> posCorr.get(i).append(radiusCorr.get(i)))));
  }
}
