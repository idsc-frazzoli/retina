// code by jph
package ch.ethz.idsc.retina.lidar;

import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SpacialProvider;
import junit.framework.TestCase;

public class VelodyneSpacialProviderTest extends TestCase {
  public void testLimitLo() {
    Vlp16SpacialProvider vlp16SpacialProvider = new Vlp16SpacialProvider(0);
    double limitLo = vlp16SpacialProvider.getLimitLo();
    assertTrue(0 < limitLo);
    assertTrue(limitLo <= 1);
  }
}
