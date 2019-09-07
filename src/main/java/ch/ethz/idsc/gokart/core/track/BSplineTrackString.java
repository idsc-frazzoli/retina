// code by mh
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class BSplineTrackString extends BSplineTrack {
  private static final Scalar ZERO = RealScalar.of(0.0);

  public BSplineTrackString(Tensor points_xyr) {
    super(points_xyr, false);
  }

  @Override // from BSplineTrack
  public Scalar getNearestPathProgress(Tensor position) {
    int effPoints = effPoints();
    if (effPoints <= 0)
      return ZERO;
    float gPosX = position.Get(0).number().floatValue();
    float gPosY = position.Get(1).number().floatValue();
    // first control point
    float bestDist = Float.MAX_VALUE; // 3.4028235e+38f
    int bestGuess = 0;
    // initial guesses
    for (int i = 0; i < effPoints; ++i) {
      int index = i * LOOKUP_SKIP;
      // quadratic distances
      float dist = getFastQuadraticDistance(index, gPosX, gPosY);
      if (dist < bestDist) {
        bestDist = dist;
        bestGuess = index;
      }
    }
    // refinement
    int precision = 4;
    while (precision > 1) {
      int upper = bestGuess + precision;
      if (upper >= posX.length)
        upper = posX.length - 1;
      if (upper < 0)
        upper = 0;
      float upperDist = getFastQuadraticDistance(upper, gPosX, gPosY);
      int lower = bestGuess - precision;
      if (lower >= posX.length)
        lower = posX.length - 1;
      if (lower < 0)
        lower = 0;
      float lowerDist = getFastQuadraticDistance(lower, gPosX, gPosY);
      if (lowerDist < bestDist) {
        bestGuess = lower;
        bestDist = lowerDist;
      } else //
      if (upperDist < bestDist) {
        bestGuess = upper;
        bestDist = upperDist;
      } else
        precision /= 2;
    }
    return RealScalar.of(bestGuess * LOOKUP_RES);
  }
}
