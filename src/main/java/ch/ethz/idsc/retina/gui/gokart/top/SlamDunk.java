// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.UniformResample;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** first localization algorithm deployed on the gokart.
 * the iterative method is used since December 2017.
 * 
 * the localization algorithm relies on a map that encodes
 * free space and obstacles.
 * 
 * confirmed to work well at speeds of up to 2[m/s] following
 * the oval trajectory in the dubendorf hangar */
public enum SlamDunk {
  ;
  /** the list of points is typically provided by {@link UniformResample}
   * 
   * @param se2MultiresSamples
   * @param geometricLayer
   * @param points with dimension n x 2 {{px_1, py_1}, ..., {px_n, py_n}}
   * @param slamScore
   * @return */
  public static SlamResult of( //
      Se2MultiresSamples se2MultiresSamples, //
      GeometricLayer geometricLayer, //
      Tensor points, //
      SlamScore slamScore) {
    Tensor result = IdentityMatrix.of(3);
    int score = -1;
    for (int level = 0; level < se2MultiresSamples.levels(); ++level) {
      score = -1;
      Tensor best = null;
      for (Tensor delta : se2MultiresSamples.level(level)) { // TODO can do in parallel
        geometricLayer.pushMatrix(delta);
        int eval = points.stream().map(geometricLayer::toPoint2D) //
            .mapToInt(slamScore::evaluate).sum();
        if (score < eval) {
          best = delta;
          score = eval;
        }
        geometricLayer.popMatrix();
      }
      geometricLayer.pushMatrix(best); // manifest for next level
      result = result.dot(best);
    }
    IntStream.range(0, se2MultiresSamples.levels()) //
        .forEach(index -> geometricLayer.popMatrix());
    return new SlamResult(result, RationalScalar.of(score, points.length() * 255));
  }
}
