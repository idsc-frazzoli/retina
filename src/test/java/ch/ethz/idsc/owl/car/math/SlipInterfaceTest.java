// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.CHatchbackModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SlipInterfaceTest extends TestCase {
  public void testSimple() {
    VehicleModel c = CHatchbackModel.standard();
    new RobustSlip(c.wheel(0).pacejka(), Tensors.vector(0, 0), RealScalar.ZERO).slip();
    try {
      new TextbookSlip(c.wheel(1).pacejka(), Tensors.vector(0, 0), RealScalar.ZERO).slip();
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testEquality1() {
    VehicleModel c = CHatchbackModel.standard();
    SlipInterface si1 = new RobustSlip(c.wheel(0).pacejka(), Tensors.vector(1, 0), RealScalar.of(1));
    SlipInterface si2 = new TextbookSlip(c.wheel(1).pacejka(), Tensors.vector(1, 0), RealScalar.of(1));
    assertEquals(si1.slip(), si2.slip());
  }

  public void testEquality2() {
    VehicleModel c = CHatchbackModel.standard();
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar rtw = RandomVariate.of(distribution);
      SlipInterface si1 = new RobustSlip(c.wheel(0).pacejka(), Tensors.vector(1, 0), rtw);
      SlipInterface si2 = new TextbookSlip(c.wheel(1).pacejka(), Tensors.vector(1, 0), rtw);
      assertTrue(Chop._10.close(si1.slip(), si2.slip()));
    }
  }

  public void testEquality3() {
    VehicleModel c = CHatchbackModel.standard();
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar vx = RandomVariate.of(distribution);
      Scalar vy = RandomVariate.of(distribution);
      Scalar rtw = RandomVariate.of(distribution);
      SlipInterface si1 = new RobustSlip(c.wheel(1).pacejka(), Tensors.of(vx, vy), rtw);
      SlipInterface si2 = new TextbookSlip(c.wheel(0).pacejka(), Tensors.of(vx, vy), rtw);
      assertTrue(Chop._10.close(si1.slip(), si2.slip()));
    }
  }
}
