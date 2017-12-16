// code by jph
package ch.ethz.idsc.retina.util.curve;

import ch.ethz.idsc.tensor.Tensor;

public interface InterpolatingCurveSubdivision {
  Tensor midpoint(Tensor B, Tensor C);

  /** insert between A, B */
  Tensor midpoint(Tensor A, Tensor B, Tensor C);

  /** insert between B, C */
  Tensor midpoint(Tensor A, Tensor B, Tensor C, Tensor D);
}
