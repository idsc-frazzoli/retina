// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RimoAxleConfigurationTest extends TestCase {
  public void testSimple() {
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(Quantity.of(0.5, "SCE"));
    axleConfiguration.wheel(0).adjoint();
    axleConfiguration.wheel(1).adjoint();
  }
}
