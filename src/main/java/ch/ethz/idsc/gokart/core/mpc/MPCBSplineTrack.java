// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.gokart.core.map.BSplineTrack;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Ramp;

public class MPCBSplineTrack implements MPCPreviewableTrack {
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);

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
  @Override // from MPCPreviewableTrack
  public MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding) {
    return getPathParameterPreview(previewSize, position, padding, _1, _1);
  }

  @Override // from MPCPreviewableTrack
  public MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding, Scalar QPFactor, Scalar qpLimit) {
    // test if this function is fast enough to be called many times (it should be)
    Scalar pathProgress = bSplineTrack.getNearestPathProgress(position);
    // round down
    // int currentIndex = Floor.of(pathProgress.subtract(RealScalar.of(0.5))).number().intValue();
    int currentIndex = Floor.of(pathProgress).number().intValue();
    // progress = 0 at middle point between first 2 control points
    Scalar progressStart = pathProgress.subtract(RealScalar.of(currentIndex));
    // QP offset
    Scalar QPOffset = progressStart;
    Tensor matrix = Tensors.empty();
    if (currentIndex < 0)
      currentIndex += bSplineTrack.numPoints();
    for (int i = 0; i < previewSize; ++i) {
      if (!bSplineTrack.isClosed() && currentIndex >= bSplineTrack.numPoints()) {
        int length = matrix.length();
        Tensor secondlast = matrix.get(length - 2);
        Tensor last = matrix.get(length - 1);
        Tensor posDif = last.subtract(secondlast).extract(0, 2);
        Tensor normDif = Normalize.with(Norm._2).apply(posDif);
        Tensor newPos = last.extract(0, 2).add(normDif.multiply(Quantity.of(0.1, SI.METER)));
        Tensor newRad = last.Get(2);
        newPos.append(newRad);
        matrix.append(newPos);
      } else {
        Tensor vector = bSplineTrack.combinedControlPoints().get(currentIndex);
        Scalar localProgress = RealScalar.of(i).subtract(QPOffset).divide(RealScalar.of(previewSize));
        Scalar localQPFactor;
        if (!QPFactor.equals(_1))
          localQPFactor = Max.of(qpLimit, QPFactor.multiply(localProgress).add(_1.subtract(localProgress)));
        else
          localQPFactor = _1;
        vector.set(scalar -> Ramp.FUNCTION.apply(((Scalar) scalar).subtract(padding).multiply(localQPFactor)), 2);
        matrix.append(vector);
        ++currentIndex;
        if (currentIndex >= bSplineTrack.numPoints())
          currentIndex = 0;
      }
    }
    return new MPCPathParameter(progressStart, matrix);
  }

  @Override // from MPCPreviewableTrack
  public Tensor getStartPose() {
    Tensor pos = bSplineTrack.getPositionXY(_0);
    Tensor dir = bSplineTrack.getDirectionXY(_0);
    Scalar angle = ArcTan2D.of(dir);
    return pos.append(angle);
  }

  public BSplineTrack bSplineTrack() {
    return bSplineTrack;
  }
}
