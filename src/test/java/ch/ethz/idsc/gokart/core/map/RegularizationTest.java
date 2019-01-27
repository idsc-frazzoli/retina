// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RegularizationTest extends TestCase {
  private static final Regularization REGULARIZATION = new Regularization(RnGeodesic.INSTANCE, RationalScalar.HALF);

  public void testEmpty() {
    assertEquals(REGULARIZATION.apply(Tensors.empty(), false), Tensors.empty());
    assertEquals(REGULARIZATION.apply(Tensors.empty(), true), Tensors.empty());
  }

  public void testSingle() {
    assertEquals(REGULARIZATION.apply(Tensors.vector(2), false), Tensors.vector(2));
    assertEquals(REGULARIZATION.apply(Tensors.vector(3), true), Tensors.vector(3));
  }

  public void testSimple() {
    Tensor signal = Tensors.vector(1, 1, 1, 2, 1, 1, 1, 1, 1, 1);
    Tensor tensor = REGULARIZATION.apply(signal, false);
    ExactScalarQ.requireAll(tensor);
    assertEquals(tensor, Tensors.fromString("{1, 1, 5/4, 3/2, 5/4, 1, 1, 1, 1, 1}"));
  }

  public void testMatrix() {
    Tensor signal = Tensors.fromString("{{1,2},{2,2},{3,2},{4,2},{3,3}}");
    Tensor tensor = REGULARIZATION.apply(signal, false);
    ExactScalarQ.requireAll(tensor);
    assertEquals(tensor, Tensors.fromString("{{1, 2}, {2, 2}, {3, 2}, {7/2, 9/4}, {3, 3}}"));
  }

  public void testZero() {
    Tensor signal = Tensors.vector(1, 1, 1, 2, 1, 1, 3, 1, 1, 1);
    Tensor tensor = new Regularization(RnGeodesic.INSTANCE, RealScalar.ZERO).apply(signal, false);
    ExactScalarQ.requireAll(tensor);
    assertEquals(tensor, signal);
  }
}
