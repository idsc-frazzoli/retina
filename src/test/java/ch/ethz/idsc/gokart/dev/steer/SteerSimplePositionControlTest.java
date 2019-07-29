// code by rvmoos and jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerSimplePositionControlTest extends TestCase {
  public void testSimple() {
    SimpleSteerPositionControl simpleSteerPositionControl = new SimpleSteerPositionControl();
    simpleSteerPositionControl.iterate(Quantity.of(1, SteerPutEvent.UNIT_ENCODER));
    simpleSteerPositionControl.iterate(Quantity.of(1, SteerPutEvent.UNIT_ENCODER));
    simpleSteerPositionControl.iterate(Quantity.of(1, SteerPutEvent.UNIT_ENCODER));
    for (int i = 0; i < 10; i++) {
      Scalar value = simpleSteerPositionControl.iterate(Quantity.of(0, SteerPutEvent.UNIT_ENCODER));
      SteerPutEvent.RTORQUE.apply(value);
    }
  }

  public void testSimple2() {
    SimpleSteerPositionControl simpleSteerPositionControl = new SimpleSteerPositionControl();
    Distribution distribution = NormalDistribution.standard();
    for (int i = 0; i < 100; i++) {
      Scalar err_pos = Quantity.of(RandomVariate.of(distribution), SteerPutEvent.UNIT_ENCODER);
      Scalar value = simpleSteerPositionControl.iterate(err_pos.multiply(RealScalar.of(0.01)));
      SteerPutEvent.RTORQUE.apply(value);
    }
  }

  public void testDt() {
    assertTrue(Scalars.lessEquals(SimpleSteerPositionControl.DT, Quantity.of(0.02, "s")));
  }
}
