// code by jph
package ch.ethz.idsc.retina.util.math;

import java.io.IOException;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Covariance2DTest extends TestCase {
  /* package */ static Tensor explicit(Scalar a, Scalar b, Scalar angle) {
    Tensor rotation = RotationMatrix.of(angle);
    Tensor diagonal = DiagonalMatrix.of(a, b);
    return rotation.dot(diagonal).dot(Transpose.of(rotation));
  }

  public void testSimple() {
    Covariance2D covariance2d = new Covariance2D(Tensors.matrix(new Number[][] { { 3, 0.5 }, { 0.5, 2 } }));
    double rotAngle = covariance2d.angle().number().doubleValue();
    assertEquals(rotAngle, 0.3926990816987242, 1e-10);
    Tensor stdDev = covariance2d.stdDev();
    VectorQ.requireLength(stdDev, 2);
    assertEquals(stdDev.Get(0).number().floatValue(), 1.7908397, 1e-6);
    assertEquals(stdDev.Get(1).number().floatValue(), 1.3389896, 1e-6);
  }

  public void testSimple2() {
    Covariance2D covariance2d = new Covariance2D(Tensors.matrix(new Number[][] { { 3, -0.5 }, { -0.5, 2 } }));
    double rotAngle = covariance2d.angle().number().doubleValue();
    assertEquals(rotAngle, -0.3926990816987242, 1e-10);
    Tensor stdDev = covariance2d.stdDev();
    VectorQ.requireLength(stdDev, 2);
    assertEquals(stdDev.Get(0).number().floatValue(), 1.7908397, 1e-6);
    assertEquals(stdDev.Get(1).number().floatValue(), 1.3389896, 1e-6);
  }

  public void testReconstruct() throws ClassNotFoundException, IOException {
    Covariance2D covariance2d = //
        Serialization.copy(Covariance2D.of(RationalScalar.HALF, RealScalar.ONE, RealScalar.ZERO));
    Scalar angle = covariance2d.angle();
    Chop._06.requireClose(angle, Pi.HALF);
    Chop._06.requireClose(covariance2d.stdDev(), Tensors.vector(1.0, 0.7071067811865476));
  }

  public void testThree() {
    Scalar rotAngle = RealScalar.of(-0.3);
    Covariance2D covariance2d = Covariance2D.of(RealScalar.of(2), RealScalar.of(1), rotAngle);
    Chop._12.requireClose(covariance2d.angle(), rotAngle);
  }

  public void testPhase() {
    Scalar rotAngle = RealScalar.of(-0.3);
    Covariance2D covariance2d = Covariance2D.of(RealScalar.of(1), RealScalar.of(2), rotAngle);
    Chop._12.requireClose(covariance2d.angle(), rotAngle.add(Pi.HALF));
  }

  public void testIndex() {
    Distribution distribution = UniformDistribution.unit();
    Distribution angleDistribution = NormalDistribution.of(0, 10);
    for (int count = 0; count < 100; ++count) {
      Scalar a = RandomVariate.of(distribution);
      Scalar b = RandomVariate.of(distribution);
      Scalar angle = RandomVariate.of(angleDistribution);
      Chop._12.requireClose(Covariance2D.matrix(a, b, angle), explicit(a, b, angle));
      Covariance2D covariance2d = Covariance2D.of(a, b, angle);
      int index = ArgMax.of(covariance2d.eigensystem().values().map(Scalar::abs));
      assertEquals(index, 0);
    }
  }

  public void testFailDimension() {
    try {
      new Covariance2D(IdentityMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailScalar() {
    try {
      new Covariance2D(RealScalar.of(2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailSymmetry() {
    try {
      new Covariance2D(Tensors.fromString("{{1, 2}, {3, 4}}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
