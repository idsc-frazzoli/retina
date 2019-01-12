// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import junit.framework.TestCase;

public class DavisDvsBlockPublisherTest extends TestCase {
  public void testSimple() {
    String channel = DavisDvsBlockPublisher.channel("overview");
    assertEquals(channel, "davis240c.overview.dvs");
  }
}
