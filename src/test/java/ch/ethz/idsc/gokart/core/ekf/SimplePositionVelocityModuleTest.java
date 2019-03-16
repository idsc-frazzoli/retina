// code by jph
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class SimplePositionVelocityModuleTest extends TestCase {
  public void testSimple() {
    SimplePositionVelocityModule simplePositionVelocityModule = new SimplePositionVelocityModule();
    simplePositionVelocityModule.first();
    assertEquals(simplePositionVelocityModule.getPose(), Tensors.fromString("{0[m], 0[m], 0}"));
    simplePositionVelocityModule.integrateImu(Tensors.fromString("{1[m*s^-2],0[m*s^-2]}"), Quantity.of(0, SI.PER_SECOND), Quantity.of(1, SI.SECOND));
    assertEquals(simplePositionVelocityModule.local_filteredVelocity, Tensors.fromString("{1[m*s^-1], 0[m*s^-1]}"));
    assertEquals(simplePositionVelocityModule.getPose(), Tensors.fromString("{1[m], 0[m], 0}"));
    // ---
    simplePositionVelocityModule.integrateImu( //
        Tensors.fromString("{0[m*s^-2],0[m*s^-2]}"), //
        Quantity.of(Math.PI / 2, SI.PER_SECOND), //
        Quantity.of(1, SI.SECOND));
    // {1[m*s^-1], -1.5707963267948966[m*s^-1]}
    // System.out.println(simplePositionVelocityModule.local_filteredVelocity);
    // {6.123233995736766E-17[m*s^-1], -1.0[m*s^-1]}
    Chop._12.requireClose(simplePositionVelocityModule.local_filteredVelocity, Tensors.fromString("{0[m*s^-1], -1[m*s^-1]}"));
    Tensor pose = simplePositionVelocityModule.getPose();
    Sign.requirePositive(pose.Get(0));
    Sign.requirePositive(pose.Get(2));
    // {1.3729232285780566[m], -0.9003163161571061[m], 0.7853981633974483}
    // System.out.println(simplePositionVelocityModule.getPose());
    simplePositionVelocityModule.last();
  }
}
