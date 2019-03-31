// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.stream.IntStream;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
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
 * confirmed to work well at speeds of up to 6[m*s^-1] and
 * rotational rates of up to 180[deg*s^-1] in combination with a gyro
 * confirmed to work well at speeds of up to 10[m*s^-1] and
 * rotational rates of up to 180[deg*s^-1] in combination with an imu */
public class SlamDunk {
  private final Se2MultiresGrids se2MultiresGrids;
  private final SlamScore slamScore;

  public SlamDunk(Se2MultiresGrids se2MultiresGrids, SlamScore slamScore) {
    this.se2MultiresGrids = se2MultiresGrids;
    this.slamScore = slamScore;
  }

  /** the list of points is typically provided by ParametricResample
   * 
   * @param geometricLayer
   * @param points with dimension n x 2 {{px_1, py_1}, ..., {px_n, py_n}}
   * @return */
  public SlamResult evaluate(GeometricLayer geometricLayer, Tensor points) {
    Tensor result = IdentityMatrix.of(3);
    int score = -1;
    System.out.println("------------");
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
      System.out.println("level=" + level + " " + best.tangent());
    }
    IntStream.range(0, se2MultiresGrids.levels()) //
        .forEach(index -> geometricLayer.popMatrix());
    Scalar quality = RealScalar.of(score / (points.length() * 255.0));
    if (!LocalizationConfig.GLOBAL.isQualityOk(quality))
      System.err.println(quality);
    return new SlamResult(result, quality);
  }
}
