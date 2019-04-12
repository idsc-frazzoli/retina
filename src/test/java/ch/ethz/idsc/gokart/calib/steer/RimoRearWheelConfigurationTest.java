// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RimoRearWheelConfigurationTest extends TestCase {
  public void testSimple() {
    TwoWheelConfiguration twoWheelConfiguration = RimoRearWheelConfiguration.INSTANCE;
    Chop._10.requireClose( //
        twoWheelConfiguration._left().local(), //
        Tensors.fromString("{0[m], +0.54[m], 0}"));
    Chop._10.requireClose( //
        twoWheelConfiguration.right().local(), //
        Tensors.fromString("{0[m], -0.54[m], 0}"));
  }
}
