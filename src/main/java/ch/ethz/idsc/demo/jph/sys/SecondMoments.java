// code by jph
package ch.ethz.idsc.demo.jph.sys;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.CholeskyDecomposition;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sign;

class SecondMoments {
  public static SecondMoments add(SecondMoments sm1, SecondMoments sm2) {
    Scalar mass = sm1.mass.add(sm2.mass);
    Scalar index = Clip.function(RealScalar.ZERO, mass).rescale(sm2.mass);
    Tensor center = LinearInterpolation.of(Tensors.of(sm1.center, sm2.center)).at(index);
    Tensor is1 = Inertias.shift(sm1.inertia, sm1.mass, sm1.center.subtract(center));
    Tensor is2 = Inertias.shift(sm2.inertia, sm2.mass, sm2.center.subtract(center));
    Tensor inertia = is1.add(is2);
    return new SecondMoments(mass, center, inertia);
  }

  // ---
  private final Scalar mass;
  private final Tensor center;
  private final Tensor inertia;

  public SecondMoments(Scalar mass, Tensor center, Tensor inertia) {
    this.mass = Sign.requirePositive(mass);
    this.center = VectorQ.requireLength(center, inertia.length());
    this.inertia = SquareMatrixQ.require(inertia);
  }

  public void printInfo() {
    System.out.println("mass   =" + mass);
    System.out.println("center =" + center);
    System.out.println("inertia=" + Pretty.of(inertia));
  }

  public static void main(String[] args) {
    SecondMoments sm1 = new SecondMoments(RealScalar.of(2), Tensors.vector(+3, 0, 0), DiagonalMatrix.of(3, 3, 3));
    SecondMoments sm2 = new SecondMoments(RealScalar.of(1), Tensors.vector(-6, 1, 2), DiagonalMatrix.of(2, 2, 2));
    SecondMoments sm3 = add(sm1, sm2);
    sm3.printInfo();
    CholeskyDecomposition choleskyDecomposition = CholeskyDecomposition.of(sm3.inertia);
    System.out.println("diagonal=" + choleskyDecomposition.diagonal());
    Eigensystem eigensystem = Eigensystem.ofSymmetric(sm3.inertia);
    System.out.println("values  =" + eigensystem.values());
    System.out.println(Pretty.of(eigensystem.vectors().map(Round._3)));
  }
}
