// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.gokart.core.map.BSplineTrack;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Round;

public class MPCBSplineTrack implements MPCPreviewableTrack {
  private static final Scalar ONE = RealScalar.ONE;
  private static final Scalar HALF = RealScalar.of(0.5);

  /** @param points_xyr matrix with dimension n x 3
   * @param radiusOffset
   * @param closed */
  public static MPCBSplineTrack withOffset(Tensor points_xyr, Scalar radiusOffset, boolean closed) {
    Tensor tensor = points_xyr.copy();
    tensor.set(radiusOffset::add, Tensor.ALL, 2);
    return new MPCBSplineTrack(tensor, closed);
  }

  private final BSplineTrack bSplineTrack;

  /** @param points_xyr matrix with dimension n x 3
   * @param closed */
  public MPCBSplineTrack(Tensor points_xyr, boolean closed) {
    bSplineTrack = new BSplineTrack(points_xyr, closed);
  }

  // TODO JPH optimize
  @Override
  public MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding) {
    return getPathParameterPreview(previewSize, position, padding, ONE);
  }

  @Override
  public MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding, Scalar QPFactor) {
    // test if this function is fast enough to be called many times (it should be)
    Scalar pathProgress = bSplineTrack.getNearestPathProgress(position);
    // round down
    // int currentIndex = Floor.of(pathProgress.subtract(RealScalar.of(0.5))).number().intValue();
    int currentIndex = Round.of(pathProgress).number().intValue() - 1;
    // progress = 0 at middle point between first 2 control points
    Scalar progressStart = pathProgress.subtract(RealScalar.of(currentIndex)).subtract(RealScalar.of(0.5));
    // QP offset
    Scalar QPOffset = pathProgress.subtract(HALF);
    Tensor matrix = Tensors.empty();
    if (currentIndex < 0)
      currentIndex += bSplineTrack.numPoints();
    for (int i = 0; i < previewSize; ++i) {
      Tensor vector = bSplineTrack.combinedControlPoints().get(currentIndex);
      Scalar localProgress = RealScalar.of(i).subtract(QPOffset).divide(RealScalar.of(previewSize));
      Scalar localQPFactor;
      if (!QPFactor.equals(ONE))
        localQPFactor = QPFactor.multiply(localProgress).add(ONE.subtract(localProgress));
      else
        localQPFactor = ONE;
      vector.set(scalar -> Ramp.FUNCTION.apply(((Scalar) scalar).subtract(padding).multiply(localQPFactor)), 2);
      matrix.append(vector);
      ++currentIndex;
      if (currentIndex >= bSplineTrack.numPoints())
        currentIndex = 0;
    }
    return new MPCPathParameter(progressStart, matrix);
  }

  public BSplineTrack bSplineTrack() {
    return bSplineTrack;
  }
}
