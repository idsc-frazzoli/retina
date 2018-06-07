// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.tensor.DoubleScalar;
import junit.framework.TestCase;

public class EmergencyBrakeProviderTest extends TestCase {
  public void testMargin() {
    assertEquals(EmergencyBrakeProvider.INSTANCE.margin(), DoubleScalar.of(1.66));
  }
}
