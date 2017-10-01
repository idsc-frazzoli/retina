// code by rvmoos and jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class PDSteerPositionControlTest extends TestCase {
  @SuppressWarnings("unused")
  public void testSimple() {
    PDSteerPositionControl test = new PDSteerPositionControl();
    test.iterate(1);
    test.iterate(1);
    test.iterate(1);
    for (int i = 0; i < 10; i++) {
      double value = test.iterate(0);
      // System.out.println(value);
    }
  }

  @SuppressWarnings("unused")
  public void testSimple2() {
    PDSteerPositionControl test = new PDSteerPositionControl();
    // test.iterate(1);
    // test.iterate(1);
    // test.iterate(1);
    Distribution distribution = NormalDistribution.standard();
    for (int i = 0; i < 100; i++) {
      Scalar err_pos = RandomVariate.of(distribution);
      double value = test.iterate(err_pos.number().doubleValue() * .01);
      // System.out.println(value);
    }
  }
}
