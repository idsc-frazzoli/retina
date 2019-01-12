// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import ch.ethz.idsc.retina.davis.DavisApsType;
import junit.framework.TestCase;

public class DavisApsBlockPublisherTest extends TestCase {
  public void testSimple() {
    assertEquals(DavisApsBlockPublisher.channel("overview", DavisApsType.SIG), "davis240c.overview.sig");
    assertEquals(DavisApsBlockPublisher.channel("overview", DavisApsType.RST), "davis240c.overview.rst");
  }
}
