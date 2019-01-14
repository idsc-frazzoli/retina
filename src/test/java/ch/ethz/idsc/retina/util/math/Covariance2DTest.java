// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Covariance2DTest extends TestCase {
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
      new Covariance2D(Tensors.fromString("{{1,2},{3,4}}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
