// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.ArcLengthParametrization;
import ch.ethz.idsc.sophus.itp.Distances;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

// TODO JPH OWL move to owl
public class RnUniformResample extends AbstractUniformResample {
  public static CurveSubdivision of(Scalar spacing) {
    return new RnUniformResample(spacing);
  }

  // ---
  private RnUniformResample(Scalar spacing) {
    super(spacing);
  }

  @Override // from AbstractUniformResample
  public Tensor distances(Tensor tensor) {
    return Distances.of(RnMetric.INSTANCE, tensor);
  }

  @Override // from AbstractUniformResample
  public ScalarTensorFunction arcLength(Tensor distances, Tensor tensor) {
    return ArcLengthParametrization.of(distances, RnGeodesic.INSTANCE, tensor);
  }
}
