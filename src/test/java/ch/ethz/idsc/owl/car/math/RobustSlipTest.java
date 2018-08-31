// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RobustSlipTest extends TestCase {
  private static void _isContinuous(Pacejka3 pacejka3, Tensor U) {
    final Tensor muLim = new RobustSlip(pacejka3, U, U.Get(0)).slip();
    final Scalar eps = RealScalar.of(1e-10);
    {
      Tensor slip = new RobustSlip(pacejka3, U, U.Get(0).add(eps)).slip();
      boolean continuous = Chop._08.close(muLim, slip);
      assertTrue(continuous);
    }
    {
      Tensor slip = new RobustSlip(pacejka3, U, U.Get(0).subtract(eps)).slip();
      boolean cont = Chop._08.close(muLim, slip);
      assertTrue(cont);
    }
  }

  public void testSimple() {
    final Pacejka3 pacejka3 = new Pacejka3(13.8509, 1.3670, 0.9622);
    _isContinuous(pacejka3, Tensors.vector(-1, -1));
    _isContinuous(pacejka3, Tensors.vector(1, -1));
    _isContinuous(pacejka3, Tensors.vector(-1, 1));
    _isContinuous(pacejka3, Tensors.vector(1, 1));
    _isContinuous(pacejka3, Tensors.vector(1, 0));
    // not continuous at:
    // _isContinuous(pacejka3, Tensors.vector(0, 1));
    // _isContinuous(pacejka3, Tensors.vector(0, 0));
  }

  public void testSimple2() {
    final Pacejka3 pacejka3 = new Pacejka3(13.8509, 1.3670, 0.9622);
    {
      SlipInterface rs = new RobustSlip(pacejka3, Tensors.vector(1, 0), RealScalar.of(1.1));
      rs.slip();
    }
    {
      SlipInterface rs = new RobustSlip(pacejka3, Tensors.vector(-1, 0), RealScalar.of(-1.1));
      rs.slip();
    }
    {
      SlipInterface rs = new RobustSlip(pacejka3, Tensors.vector(1, 0), RealScalar.of(0.9));
      rs.slip();
    }
    {
      SlipInterface rs = new RobustSlip(pacejka3, Tensors.vector(-1, 0), RealScalar.of(-0.9));
      rs.slip();
    }
  }
}
