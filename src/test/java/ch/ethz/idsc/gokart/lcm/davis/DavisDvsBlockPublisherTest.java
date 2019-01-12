// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import junit.framework.TestCase;

public class DavisDvsBlockPublisherTest extends TestCase {
  public void testSimple() {
    String channel = DavisDvsBlockPublisher.channel("overview");
    assertEquals(channel, "davis240c.overview.dvs");
  }
}
