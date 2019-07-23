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
  private final RegionRayTrace regionRayTrace;

  public TrackRefinement(Region<Tensor> region) {
    regionRayTrace = new RegionRayTrace( //
        region, //
        Quantity.of(0.1, SI.METER), //
        Quantity.of(1, SI.METER), // probably max center line shift
        Quantity.of(10, SI.METER)); // probably max boundary shift
  }

  private static final Scalar gdRadiusGrowth = Quantity.of(0.07, SI.METER);
  private static final Scalar gdRegularizer = RealScalar.of(0.007);
  private static final Scalar gdLimits = RealScalar.of(1);
  private static final Scalar gdRadius = RealScalar.of(1);
  private static final TensorUnaryOperator REGULARIZATION_CYCLIC = Regularization2Step.cyclic(RnGeodesic.INSTANCE, gdRegularizer);
  private static final TensorUnaryOperator REGULARIZATION_STRING = Regularization2Step.string(RnGeodesic.INSTANCE, gdRegularizer);

  Tensor getRefinedTrack(Tensor points_xyr, Scalar resolution, int iterations, boolean closed) {
    int m = (int) (points_xyr.length() * resolution.number().doubleValue());
    int n = points_xyr.length();
    Tensor queryPositions;
    if (closed)
      queryPositions = Tensors.vector(i -> RealScalar.of((n + 0.0) * (i / (m + 0.0))), m);
    else
      // TODO MH try Subdivide.of(0, n-2, m-1) for the below
      // FIXME m-1 vs m
      queryPositions = Tensors.vector(i -> RealScalar.of((n - 2.0) * (i / (m - 1.0))), m - 1);
    Tensor splineMatrix = queryPositions.map(BSpline2Vector.of(n, 0, closed));
    Tensor splineMatrixTransp = Transpose.of(splineMatrix);
    Tensor splineMatrix1Der = queryPositions.map(BSpline2Vector.of(n, 1, closed));
    int iteration = 0;
    while (iteration < iterations) {
      System.out.println("iterate " + iteration);
      Optional<Tensor> optional = //
          getCorrectionVectors(points_xyr, splineMatrix, splineMatrix1Der, resolution);
      if (!optional.isPresent())
        return null;
      Tensor correct = optional.get();
      points_xyr = points_xyr.add(splineMatrixTransp.dot(correct));
      points_xyr.set(gdRadiusGrowth::add, Tensor.ALL, 2);
      points_xyr = closed //
          ? REGULARIZATION_CYCLIC.apply(points_xyr)
          : REGULARIZATION_STRING.apply(points_xyr);
      ++iteration;
    }
    System.out.println("Iterate " + iteration + " times!");
    return points_xyr;
  }

  // for debugging
  // TODO JPH/MH design is bad
  private List<Tensor> freeLines = new ArrayList<>();

  /** .
   * @param points_xyr
   * @param basisMatrix
   * @param basisMatrix1Der
   * @param resolution
   * @return */
  private Optional<Tensor> getCorrectionVectors( //
      Tensor points_xyr, Tensor basisMatrix, Tensor basisMatrix1Der, Scalar resolution) {
    // ---
    Tensor positionsXYR = basisMatrix.dot(points_xyr);
    Tensor sideVectors = BSplineUtil.getSidewardsUnitVectors( //
        Tensor.of(points_xyr.stream().map(Extract2D.FUNCTION)), //
        basisMatrix1Der);
    freeLines = new ArrayList<>();
    List<Limit> sideLimits = new ArrayList<>();
    for (int i = 0; i < positionsXYR.length(); ++i) {
      Tensor pos_xyr = positionsXYR.get(i).extract(0, 2);
      Tensor sidevec = sideVectors.get(i);
      sideLimits.add(regionRayTrace.getLimits(pos_xyr, sidevec));
    }
    // boolean hasNoSolution = sideLimits.stream().anyMatch(row -> row.get(0).equals(row.get(1)));
    // if (hasNoSolution) {
    // // return Optional.empty();
    // }
    // upwardsforce
    Tensor clippingLo = Tensors.vector(i -> Ramp.FUNCTION.apply(positionsXYR.Get(i, 2).add(sideLimits.get(i).lo)), positionsXYR.length());
    Tensor clippingHi = Tensors.vector(i -> Ramp.FUNCTION.apply(positionsXYR.Get(i, 2).subtract(sideLimits.get(i).hi)), positionsXYR.length());
    Tensor sideCorr = clippingLo.subtract(clippingHi).multiply(gdLimits.divide(resolution));
    Tensor posCorr = sideCorr.pmul(sideVectors);
    Tensor radiusCorr = clippingHi.add(clippingLo).multiply(gdRadius.divide(resolution).negate());
    return Optional.of(Tensor.of(IntStream.range(0, posCorr.length()).mapToObj(i -> posCorr.get(i).append(radiusCorr.get(i)))));
  }
}
