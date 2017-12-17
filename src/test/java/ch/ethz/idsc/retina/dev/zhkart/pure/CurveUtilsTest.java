// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CurveUtilsTest extends TestCase {
  public void testNoMatch() {
    int index = CurveUtils.closestCloserThan(Tensors.fromString("{{2},{3}}"), RealScalar.ONE);
    assertEquals(index, CurveUtils.NO_MATCH);
  }

  public void testEmpty() {
    int index = CurveUtils.closestCloserThan(Tensors.fromString("{}"), RealScalar.ONE);
    assertEquals(index, CurveUtils.NO_MATCH);
  }

  public void testMatch1() {
    int index = CurveUtils.closestCloserThan(Tensors.fromString("{{0.6},{-0.4},{3}}"), RealScalar.ONE);
    assertEquals(index, 1);
  }

  public void testMatch2() {
    Tensor curve = Tensors.fromString("{{-1.2},{-0.4},{0.6},{1.4},{2.2}}");
    int index = CurveUtils.closestCloserThan(curve, RealScalar.ONE);
    assertEquals(index, 1);
    Optional<Tensor> optional = CurveUtils.interpolate(curve, index, RealScalar.ONE);
    assertTrue(optional.isPresent());
    Tensor point = optional.get();
    assertEquals(point, Tensors.vector(1));
  }

  public void testDistanceFail() {
    Tensor curve = Tensors.fromString("{{-1.2},{-0.4},{0.6},{1.4},{2.2}}");
    int index = CurveUtils.closestCloserThan(curve, RealScalar.ONE);
    assertEquals(index, 1);
    Optional<Tensor> optional = CurveUtils.interpolate(curve, index, RealScalar.of(3));
    assertFalse(optional.isPresent());
  }
}
