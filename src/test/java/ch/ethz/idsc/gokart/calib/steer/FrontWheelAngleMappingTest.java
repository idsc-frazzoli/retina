// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class FrontWheelAngleMappingTest extends TestCase {
  public void testSimple() {
    Scalar scalar = FrontWheelAngleMapping._LEFT.getAngleFromSCE(Quantity.of(0.0, "SCE"));
    assertEquals(scalar, RealScalar.ZERO);
  }

  public void testP06() {
    Scalar scalar = FrontWheelAngleMapping._LEFT.getAngleFromSCE(Quantity.of(0.6, "SCE"));
    Chop._10.requireClose(scalar, RealScalar.of(0.5374821359999999));
  }

  public void testN06() {
    Scalar scalar = FrontWheelAngleMapping._LEFT.getAngleFromSCE(Quantity.of(-0.6, "SCE"));
    Chop._10.requireClose(scalar, RealScalar.of(-0.319543176));
  }

  public void testSymmetry() {
    for (Tensor _x : Subdivide.of(-1, 1, 20)) {
      Scalar x = _x.Get();
      Scalar l = FrontWheelAngleMapping._LEFT.getAngleFromSCE(Quantity.of(x, "SCE"));
      Scalar r = FrontWheelAngleMapping.RIGHT.getAngleFromSCE(Quantity.of(x.negate(), "SCE"));
      assertEquals(l, r.negate());
    }
  }

  public void testInverseFail() {
    for (FrontWheelAngleMapping frontWheelSteerMapping : FrontWheelAngleMapping.values())
      try {
        frontWheelSteerMapping.getSCEfromAngle(RealScalar.ZERO);
        fail();
      } catch (Exception exception) {
        // ---
      }
  }
}
