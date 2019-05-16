// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class NoFrictionExperimentTest extends TestCase {
  public void testSimple() {
    NoFrictionExperiment noFrictionExperiment = new NoFrictionExperiment();
    noFrictionExperiment.first();
    assertFalse(noFrictionExperiment.putEvent().isPresent());
    noFrictionExperiment.last();
  }

  public void testTime2Torque() {
    NoFrictionExperiment noFrictionExperiment = new NoFrictionExperiment();
    assertEquals(noFrictionExperiment.time2torque(0), Quantity.of(0, "SCT"));
    Subdivide.of(0.0, 500.0, 300).stream() //
        .map(Scalar.class::cast) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .mapToObj(noFrictionExperiment::time2torque) //
        .forEach(SteerPutEvent.RTORQUE::apply);
    assertEquals(noFrictionExperiment.time2torque(10000), Quantity.of(0, "SCT"));
  }
}
