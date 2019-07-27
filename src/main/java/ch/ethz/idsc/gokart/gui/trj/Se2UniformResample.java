// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.ArcLengthParametrization;
import ch.ethz.idsc.sophus.itp.Distances;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Round;

// TODO JPH OWL move to owl
public class Se2UniformResample implements CurveSubdivision, Serializable {
  public static CurveSubdivision of(Scalar spacing) {
    return new Se2UniformResample(spacing);
  }

  // ---
  private final Scalar spacing;

  private Se2UniformResample(Scalar spacing) {
    this.spacing = spacing;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    return string(tensor.copy().append(tensor.get(0)));
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    Tensor differences = Distances.of(Se2ParametricDistance.INSTANCE, tensor);
    ScalarTensorFunction scalarTensorFunction = //
        ArcLengthParametrization.of(differences, Se2Geodesic.INSTANCE, tensor);
    Scalar length = Total.ofVector(differences);
    int n = Scalars.intValueExact(Round.FUNCTION.apply(length.divide(spacing)));
    return Tensor.of(Subdivide.of(0, 1, n).stream() //
        .limit(n) //
        .map(Scalar.class::cast) //
        .map(scalarTensorFunction));
  }
}
