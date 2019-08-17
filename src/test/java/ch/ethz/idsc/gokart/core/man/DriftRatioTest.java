// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DriftRatioTest extends TestCase {
  public void testPrevDivZero() {
    Scalar scalar = DriftRatio.INSTANCE.apply(Tensors.fromString("{0[m*s^-1], 1[m*s^-1]}"));
    assertEquals(scalar, RealScalar.ZERO);
  }

  public void testOnePos() {
    Scalar scalar = DriftRatio.INSTANCE.apply(Tensors.fromString("{1[m*s^-1], 1[m*s^-1]}"));
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testOneGyroZPos() {
    Scalar scalar = DriftRatio.INSTANCE.apply(Tensors.fromString("{1[m*s^-1], 1[m*s^-1], 2}"));
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testOneNeg() {
    Scalar scalar = DriftRatio.INSTANCE.apply(Tensors.fromString("{1[m*s^-1], -1[m*s^-1]}"));
    assertEquals(scalar, RealScalar.ONE.negate());
  }

  public void testDriftUnitFail() {
    try {
      DriftRatio.INSTANCE.apply(Tensors.fromString("{1, -1}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
