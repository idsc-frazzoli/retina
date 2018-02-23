// code by jph
package ch.ethz.idsc.owl.car.math;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TurningGeometryTest extends TestCase {
  public void test90() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.of(Math.PI / 2));
    assertTrue(Chop._10.allZero(offsetY.get()));
  }

  public void test45() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.of(Math.PI / 4));
    assertTrue(Chop._10.close(offsetY.get(), RealScalar.ONE));
  }

  public void test45neg() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.of(-Math.PI / 4));
    assertTrue(Chop._10.close(offsetY.get(), RealScalar.ONE.negate()));
  }

  public void test0() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, RealScalar.ZERO);
    assertFalse(offsetY.isPresent());
  }

  public void testClose0() {
    Optional<Scalar> offsetY = TurningGeometry.offset_y(RealScalar.ONE, TurningGeometry.ANGLE_THRESHOLD);
    assertTrue(offsetY.isPresent());
  }
}
