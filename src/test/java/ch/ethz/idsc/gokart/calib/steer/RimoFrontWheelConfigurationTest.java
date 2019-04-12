// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RimoFrontWheelConfigurationTest extends TestCase {
  public void testSimple() {
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(Quantity.of(0.2, "SCE"));
    Chop._10.requireClose( //
        axleConfiguration.wheel(0).local(), //
        Tensors.fromString("{1.19[m], +0.48[m], 0.194912248}"));
    Chop._10.requireClose( //
        axleConfiguration.wheel(1).local(), //
        Tensors.fromString("{1.19[m], -0.48[m], 0.170696808}"));
  }
}
