// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Round;

public abstract class AbstractUniformResample implements CurveSubdivision, Serializable {
  private final Scalar spacing;

  protected AbstractUniformResample(Scalar spacing) {
    this.spacing = spacing;
  }

  @Override // from CurveSubdivision
  public final Tensor cyclic(Tensor tensor) {
    return string(tensor.copy().append(tensor.get(0)));
  }

  @Override // from CurveSubdivision
  public final Tensor string(Tensor tensor) {
    Tensor distances = distances(tensor);
    Scalar length = Total.ofVector(distances);
    int n = Scalars.intValueExact(Round.FUNCTION.apply(length.divide(spacing)));
    return Tensor.of(Subdivide.of(0, 1, n).stream() //
        .limit(n) //
        .map(Scalar.class::cast) //
        .map(arcLength(distances, tensor)));
  }

  /** @param tensor
   * @return vector of distances between two successive rows in given tensor */
  protected abstract Tensor distances(Tensor tensor);

  /** @param distances
   * @param tensor
   * @return function defined over interval [0, 1] */
  protected abstract ScalarTensorFunction arcLength(Tensor distances, Tensor tensor);
}
