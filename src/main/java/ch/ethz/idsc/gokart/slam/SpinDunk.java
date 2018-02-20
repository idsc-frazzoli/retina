// code by jph
package ch.ethz.idsc.gokart.slam;

import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.ParametricResample;
import ch.ethz.idsc.retina.util.math.ResampleResult;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** 2nd localization algorithm deployed on the gokart.
 * the iterative method is used since April 2018.
 * 
 * the localization algorithm relies on a map that encodes
 * free space and obstacles.
 * 
 * TODO confirmed to work well at speeds of up to XXX[m/s] following
 * the oval trajectory in the dubendorf hangar */
public enum SpinDunk {
  ;
  /** the list of points is typically provided by {@link ParametricResample}
   * 
   * @param se2MultiresGrids
   * @param geometricLayer
   * @param points with dimension n x 2 {{px_1, py_1}, ..., {px_n, py_n}}
   * @param slamScore
   * @return */
  public static SlamResult of( //
      Se2MultiresGrids se2MultiresGrids, GeometricLayer geometricLayer, //
      ResampleResult resampleResult, SlamScore slamScore) {
    // List<Tensor> list = resampleResult.getPoints();
    // Tensor points = Tensor.of(list.stream().flatMap(Tensor::stream));
    // ---
    Tensor result = IdentityMatrix.of(3);
    int score = -1;
    Tensor offset = Array.zeros(3);
    for (int level = 0; level < se2MultiresGrids.grids(); ++level) {
      score = -1;
      Se2GridPoint best = null;
      Se2Grid se2grid = se2MultiresGrids.grid(level);
      for (Se2GridPoint se2GridPoint : se2grid.gridPoints()) {
        geometricLayer.pushMatrix(se2GridPoint.matrix());
        Scalar rate = offset.Get(2).add(se2GridPoint.tangent().Get(2));
        List<Tensor> list = resampleResult.getPointsSpin(rate);
        Tensor points = Tensor.of(list.stream().flatMap(Tensor::stream));
        int eval = points.stream().map(geometricLayer::toPoint2D) // TODO can do in parallel
            .mapToInt(slamScore::evaluate).sum();
        if (score < eval) {
          best = se2GridPoint;
          score = eval;
        }
        geometricLayer.popMatrix();
      }
      offset = offset.add(best.tangent());
      // System.out.println("offset " + offset);
      geometricLayer.pushMatrix(best.matrix()); // manifest for next level
      result = result.dot(best.matrix());
    }
    IntStream.range(0, se2MultiresGrids.grids()) //
        .forEach(index -> geometricLayer.popMatrix());
    return new SlamResult(result, RationalScalar.of(score, resampleResult.count() * 255));
  }
}
