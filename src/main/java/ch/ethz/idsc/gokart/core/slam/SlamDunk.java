// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** localization algorithm deployed on the gokart.
 * the iterative method is used since December 2017.
 * 
 * the localization algorithm relies on a map that encodes
 * free space and obstacles.
 * 
 * confirmed to work well at speeds of up to 2[m*s^-1] following
 * the oval trajectory in the Dubendorf hangar
 * confirmed to work well at speeds of up to 10[m*s^-1] and
 * rotational rates of up to 180[deg*s^-1]. */
public enum SlamDunk {
  ;
  /** the list of points is typically provided by ParametricResample
   * 
   * @param se2MultiresGrids
   * @param geometricLayer
   * @param points with dimension n x 2 {{px_1, py_1}, ..., {px_n, py_n}}
   * @param slamScore
   * @return */
  public static SlamResult of( //
      Se2MultiresGrids se2MultiresGrids, GeometricLayer geometricLayer, Tensor points, SlamScore slamScore) {
    Tensor result = IdentityMatrix.of(3);
    int score = -1;
    for (int level = 0; level < se2MultiresGrids.levels(); ++level) {
      score = -1;
      Se2GridPoint best = null;
      Se2Grid se2grid = se2MultiresGrids.grid(level);
      for (Se2GridPoint se2GridPoint : se2grid.gridPoints()) {
        geometricLayer.pushMatrix(se2GridPoint.matrix());
        int eval = points.stream() //
            .map(geometricLayer::toPoint2D) //
            .mapToInt(slamScore::evaluate).sum();
        if (score < eval) {
          best = se2GridPoint;
          score = eval;
        }
        geometricLayer.popMatrix();
      }
      geometricLayer.pushMatrix(best.matrix()); // manifest for next level
      result = result.dot(best.matrix());
    }
    IntStream.range(0, se2MultiresGrids.levels()) //
        .forEach(index -> geometricLayer.popMatrix());
    return new SlamResult(result, RationalScalar.of(score, points.length() * 255));
  }
}
