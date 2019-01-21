// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.mpc.MPCPathParameter;
import ch.ethz.idsc.gokart.core.mpc.MPCPreviewableTrack;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Round;

public class MPCBSplineTrack extends BSplineTrack implements MPCPreviewableTrack {
  final static Scalar ONE = RealScalar.ONE;
  final static Scalar HALF = RealScalar.of(0.5);
  final static Scalar ZERO = RealScalar.ZERO;
  final static Clip ONEZEROCLIP = Clip.function(ZERO, ONE);

  /** @param trackData matrix with dimension n x 3
   * @param radiusOffset
   * @param closed */
  public static MPCBSplineTrack withOffset(Tensor trackData, Scalar radiusOffset, boolean closed) {
    Tensor tensor = trackData.copy();
    tensor.set(radiusOffset::add, Tensor.ALL, 2);
    return new MPCBSplineTrack(tensor, closed);
  }

  /** @param trackData matrix with dimension n x 3
   * @param closed */
  public MPCBSplineTrack(Tensor trackData, boolean closed) {
    super(trackData, closed);
  }

  // TODO JPH optimize
  @Override
  public MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding) {
    return getPathParameterPreview(previewSize, position, padding, ONE);
  }

  @Override
  public MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding, Scalar QPFactor) {
    // test if this function is fast enough to be called many times (it should be)
    Scalar pathProgress = getNearestPathProgress(position);
    // round down
    // int currentIndex = Floor.of(pathProgress.subtract(RealScalar.of(0.5))).number().intValue();
    int currentIndex = Round.of(pathProgress).number().intValue() - 1;
    // progress = 0 at middle point between first 2 control points
    Scalar progressStart = pathProgress.subtract(RealScalar.of(currentIndex)).subtract(RealScalar.of(0.5));
    // QP offset
    Scalar QPOffset = pathProgress.subtract(HALF);
    Tensor matrix = Tensors.empty();
    if (currentIndex < 0)
      currentIndex += numPoints;
    for (int i = 0; i < previewSize; ++i) {
      Tensor vector = combinedControlPoints().get(currentIndex);
      Scalar localProgress = RealScalar.of(i).subtract(QPOffset).divide(RealScalar.of(previewSize));
      Scalar localQPFactor;
      if (!QPFactor.equals(ONE))
        localQPFactor = QPFactor.multiply(localProgress).add(ONE.subtract(localProgress));
      else
        localQPFactor = ONE;
      vector.set(scalar -> Ramp.FUNCTION.apply(((Scalar) scalar).subtract(padding).multiply(localQPFactor)), 2);
      matrix.append(vector);
      ++currentIndex;
      if (currentIndex >= numPoints)
        currentIndex = 0;
    }
    return new MPCPathParameter(progressStart, matrix);
  }
}
