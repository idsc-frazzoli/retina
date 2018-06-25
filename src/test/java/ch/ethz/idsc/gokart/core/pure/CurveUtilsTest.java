// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class CurveUtilsTest extends TestCase {
  private static final int NO_MATCH = -1;

  public void testNoMatch() {
    int index = StaticHelper.closestCloserThan(Tensors.fromString("{{2},{3}}"), RealScalar.ONE);
    assertEquals(index, NO_MATCH);
    Optional<Tensor> optional = CurveUtils.getAheadTrail(Tensors.fromString("{{2},{3}}"), RealScalar.ONE);
    assertFalse(optional.isPresent());
  }

  public void testEmpty() {
    int index = StaticHelper.closestCloserThan(Tensors.empty(), RealScalar.ONE);
    assertEquals(index, NO_MATCH);
    Optional<Tensor> optional = CurveUtils.getAheadTrail(Tensors.empty(), RealScalar.ONE);
    assertFalse(optional.isPresent());
  }

  public void testMatch1() {
    int index = StaticHelper.closestCloserThan(Tensors.fromString("{{0.6},{-0.4},{3}}"), RealScalar.ONE);
    assertEquals(index, 1);
  }

  public void testMatch2() {
    Tensor curve = Tensors.fromString("{{-1.2},{-0.4},{0.6},{1.4},{2.2}}");
    int index = StaticHelper.closestCloserThan(curve, RealScalar.ONE);
    assertEquals(index, 1);
  }

  public void testDistanceFail() {
    Tensor curve = Tensors.fromString("{{-1.2},{-0.4},{0.6},{1.4},{2.2}}");
    int index = StaticHelper.closestCloserThan(curve, RealScalar.ONE);
    assertEquals(index, 1);
  }

  public void testAnglePass() {
    Tensor xyz = Tensors.vector(35.200, 44.933, Degree.of(55).number().doubleValue());
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(xyz).inverse();
    Tensor tensor = Tensor.of(DubendorfCurve.OVAL.stream().map(tensorUnaryOperator));
    Optional<Tensor> optional = CurveUtils.getAheadTrail(tensor, RealScalar.of(3));
    assertTrue(optional.isPresent());
  }

  public void testAngleFail() {
    Tensor degrees = Tensors.vector(90 + 55, 180, 0, -20, 180 + 55);
    for (Tensor deg : degrees) {
      Tensor xyz = Tensors.vector(35.200, 44.933, Degree.of(deg.Get().number().doubleValue()).number().doubleValue());
      TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(xyz).inverse();
      Tensor tensor = Tensor.of(DubendorfCurve.OVAL.stream().map(tensorUnaryOperator));
      Optional<Tensor> optional = CurveUtils.getAheadTrail(tensor, RealScalar.of(3));
      assertFalse(optional.isPresent());
    }
  }
}
