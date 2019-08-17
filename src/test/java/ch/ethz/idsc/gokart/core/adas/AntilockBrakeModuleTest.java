// code by jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.owl.ani.api.ProviderRank;
import junit.framework.TestCase;

public class AntilockBrakeModuleTest extends TestCase {
  public void testSimple() {
    AntilockBrakeModule antilockBrakeModule = new AntilockBrakeV1Module();
    assertEquals(antilockBrakeModule.getProviderRank(), ProviderRank.EMERGENCY);
  }

  public void testNullFail() {
    try {
      new AntilockBrakeV1Module(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
