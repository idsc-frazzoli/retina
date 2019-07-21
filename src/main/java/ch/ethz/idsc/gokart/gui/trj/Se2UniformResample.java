// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.GeodesicInterpolation;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Round;

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
    Tensor curve = tensor.copy().append(tensor.get(0));
    Tensor diffs = Tensor.of(Differences.of(curve).stream().map(Extract2D.FUNCTION).map(Norm._2::ofVector));
    Interpolation interpolation = GeodesicInterpolation.of(Se2Geodesic.INSTANCE, curve);
    Distribution distribution = EqualizingDistribution.fromUnscaledPDF(diffs);
    Scalar length = Total.ofVector(diffs);
    int n = Scalars.intValueExact(Round.FUNCTION.apply(length.divide(spacing)));
    InverseCDF inverseCDF = (InverseCDF) distribution;
    return Tensor.of(Subdivide.of(0, 1, n).stream() //
        .limit(n) //
        .map(Scalar.class::cast) //
        .map(inverseCDF::quantile) //
        .map(interpolation::at));
  }
}
