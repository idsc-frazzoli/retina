// code by jph
package ch.ethz.idsc.retina.util.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** classic four point scheme by Dyn */
public enum FourPointSubdivision implements InterpolatingCurveSubdivision {
  SCHEME;
  // ---
  private static final Scalar W1 = RationalScalar.of(9, 16);
  private static final Scalar W2 = RationalScalar.of(-1, 16);
  private static final Scalar HALF = RationalScalar.of(1, 2);

  @Override
  public Tensor midpoint(Tensor A, Tensor B, Tensor C, Tensor D) {
    return B.add(C).multiply(W1).add(A.add(D).multiply(W2));
  }

  @Override
  public Tensor midpoint(Tensor B, Tensor C) {
    return B.add(C).multiply(HALF);
  }

  @Override
  public Tensor midpoint(Tensor B, Tensor C, Tensor D) {
    // the weights are computed using quadratic precision
    // return Tensors.sum(B.times(0.375), C.times(0.75), D.times(-0.125));
    throw new RuntimeException();
  }
}
