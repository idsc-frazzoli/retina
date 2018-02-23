// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class CurveUtilsTest extends TestCase {
  public void testNoMatch() {
    int index = CurveUtils.closestCloserThan(Tensors.fromString("{{2},{3}}"), RealScalar.ONE);
    assertEquals(index, CurveUtils.NO_MATCH);
    Optional<Tensor> optional = CurveUtils.getAheadTrail(Tensors.fromString("{{2},{3}}"), RealScalar.ONE);
    assertFalse(optional.isPresent());
  }

  public void testEmpty() {
    int index = CurveUtils.closestCloserThan(Tensors.empty(), RealScalar.ONE);
    assertEquals(index, CurveUtils.NO_MATCH);
    Optional<Tensor> optional = CurveUtils.getAheadTrail(Tensors.empty(), RealScalar.ONE);
    assertFalse(optional.isPresent());
  }

  public void testMatch1() {
    int index = CurveUtils.closestCloserThan(Tensors.fromString("{{0.6},{-0.4},{3}}"), RealScalar.ONE);
    assertEquals(index, 1);
  }

  public void testMatch2() {
    Tensor curve = Tensors.fromString("{{-1.2},{-0.4},{0.6},{1.4},{2.2}}");
    int index = CurveUtils.closestCloserThan(curve, RealScalar.ONE);
    assertEquals(index, 1);
    // Optional<Tensor> optional = CurveUtils.interpolate(curve, index, RealScalar.ONE);
    // assertTrue(optional.isPresent());
    // Tensor point = optional.get();
    // assertEquals(point, Tensors.vector(1));
  }

  public void testDistanceFail() {
    Tensor curve = Tensors.fromString("{{-1.2},{-0.4},{0.6},{1.4},{2.2}}");
    int index = CurveUtils.closestCloserThan(curve, RealScalar.ONE);
    assertEquals(index, 1);
    // Optional<Tensor> optional = CurveUtils.interpolate(curve, index, RealScalar.of(3));
    // assertFalse(optional.isPresent());
  }

  public void testGetAheadTrail() {
    for (int c = 0; c < 10; ++c) {
      Distribution distribution = UniformDistribution.of(-1, 1);
      Tensor tensor = RandomVariate.of(distribution, 30, 2);
      int index = CurveUtils.closestCloserThan(tensor, RealScalar.ONE);
      assertTrue(0 <= index);
      // System.out.println(index);
      Tensor trail = CurveUtils.getAheadTrail(tensor, RealScalar.ONE).get();
      assertEquals(trail.length(), 15);
      boolean status = false;
      for (int i = 0; i < 30; ++i) {
        Tensor sub = RotateLeft.of(tensor, i).extract(0, 15);
        status |= sub.equals(trail);
        if (status)
          break;
      }
      assertTrue(status);
    }
  }
}
