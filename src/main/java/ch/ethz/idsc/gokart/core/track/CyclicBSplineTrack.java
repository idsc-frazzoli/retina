package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class CyclicBSplineTrack extends BSplineTrack {
  public CyclicBSplineTrack(Tensor points_xyr) {
    super(points_xyr, true);
  }

  /** problem: using normal BSpline implementation takes more time than full MPC optimization
   * solution: fast position lookup: from 45000 micro s -> 15 micro s
   * @param position vector */
  @Override
  public Scalar getNearestPathProgress(Tensor position) {
    float gPosX = position.Get(0).number().floatValue();
    float gPosY = position.Get(1).number().floatValue();
    // first control point
    float bestDist = 10000f;
    int bestGuess = 0;
    // initial guesses
    for (int i = 0; i < numPoints; i++) {
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
        upper -= posX.length;
      if (upper < 0)
        upper += posX.length;
      float upperDist = getFastQuadraticDistance(upper, gPosX, gPosY);
      int lower = bestGuess - precision;
      if (lower >= posX.length)
        lower -= posX.length;
      if (lower < 0)
        lower += posX.length;
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
      // System.out.println("pos: "+bestGuess*lookupRes);
      // System.out.println(Math.sqrt(getFastQuadraticDistance(bestGuess, gPosX, gPosY)));
    }
    return RealScalar.of(bestGuess * LOOKUP_RES);
  }
}
