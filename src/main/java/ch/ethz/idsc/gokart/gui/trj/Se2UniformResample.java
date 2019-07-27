// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.ArcLengthParametrization;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.math.Distances;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

// TODO JPH OWL 049 move to owl
public class Se2UniformResample extends AbstractUniformResample {
  public static CurveSubdivision of(Scalar spacing) {
    return new Se2UniformResample(spacing);
  }

  // ---
  private Se2UniformResample(Scalar spacing) {
    super(spacing);
  }

  @Override // from AbstractUniformResample
  public Tensor distances(Tensor tensor) {
    return Distances.of(Se2ParametricDistance.INSTANCE, tensor);
  }

  @Override // from AbstractUniformResample
  public ScalarTensorFunction arcLength(Tensor distances, Tensor tensor) {
    return ArcLengthParametrization.of(distances, Se2Geodesic.INSTANCE, tensor);
  }
}
