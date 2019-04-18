// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RimoRearAxleConfigurationTest extends TestCase {
  public void testSimple() {
    AxleConfiguration axleConfiguration = RimoRearAxleConfiguration.INSTANCE;
    Chop._10.requireClose( //
        axleConfiguration.wheel(0).local(), //
        Tensors.fromString("{0[m], +0.54[m], 0}"));
    Chop._10.requireClose( //
        axleConfiguration.wheel(1).local(), //
        Tensors.fromString("{0[m], -0.54[m], 0}"));
  }
}
