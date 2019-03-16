// code by jph
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SimplePositionVelocityModuleTest extends TestCase {
  public void testSimple() {
    SimplePositionVelocityModule simplePositionVelocityModule = new SimplePositionVelocityModule();
    simplePositionVelocityModule.first();
    simplePositionVelocityModule.measureAcceleration(Tensors.fromString("{1[m*s^-2],0[m*s^-2]}"), Quantity.of(0, SI.PER_SECOND), Quantity.of(1, SI.SECOND));
    assertEquals(simplePositionVelocityModule.local_filteredVelocity, Tensors.fromString("{1[m*s^-1], 0[m*s^-1]}"));
    simplePositionVelocityModule.measureAcceleration(Tensors.fromString("{0[m*s^-2],0[m*s^-2]}"), Quantity.of(Math.PI / 2, SI.PER_SECOND),
        Quantity.of(1, SI.SECOND));
    // {1[m*s^-1], -1.5707963267948966[m*s^-1]}
    // System.out.println(simplePositionVelocityModule.local_filteredVelocity);
    // {6.123233995736766E-17[m*s^-1], -1.0[m*s^-1]}
    Chop._12.requireClose(simplePositionVelocityModule.local_filteredVelocity, Tensors.fromString("{0[m*s^-1], -1[m*s^-1]}"));
    simplePositionVelocityModule.last();
  }
}
