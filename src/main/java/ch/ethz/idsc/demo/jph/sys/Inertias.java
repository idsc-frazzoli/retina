// code by jph
// https://en.wikipedia.org/wiki/Parallel_axis_theorem
package ch.ethz.idsc.demo.jph.sys;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.red.Times;

enum Inertias {
  ;
  public static Tensor shift(Tensor I, Scalar m, Tensor r) {
    return I.add(IdentityMatrix.of(r.length()).multiply(r.dot(r).Get()).subtract(TensorProduct.of(r, r)).multiply(m));
  }

  /** @param I
   * @param m
   * @param r vector of length 3
   * @return */
  static Tensor shift3(Tensor I, Scalar m, Tensor r) {
    Tensor R = Cross.of(r);
    return I.subtract(R.dot(R).multiply(m));
  }

  // https://en.wikipedia.org/wiki/List_of_moments_of_inertia
  public static void main(String[] args) {
    // Slender rod along y-axis of length l and mass m about end
    Scalar m = RealScalar.of(5);
    Scalar l = RealScalar.of(7);
    Scalar f0 = RationalScalar.of(1, 12);
    Scalar c0 = Times.of(f0, m, l, l);
    Tensor I = DiagonalMatrix.of(c0, RealScalar.ZERO, c0);
    Scalar f1 = RationalScalar.of(1, 3);
    Scalar c1 = Times.of(f1, m, l, l);
    Tensor J = DiagonalMatrix.of(c1, RealScalar.ZERO, c1);
    System.out.println(Pretty.of(J));
    Scalar lh = l.divide(RealScalar.of(2));
    int k = 1;
    Tensor R = shift(I, m, UnitVector.of(3, k).multiply(lh));
    System.out.println(Pretty.of(R));
  }
}
