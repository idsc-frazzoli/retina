// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import junit.framework.TestCase;

public class DavisImuFramePublisherTest extends TestCase {
  public void testSimple() {
    String channel = DavisImuFramePublisher.channel("overview");
    assertEquals(channel, "davis240c.overview.atg");
  }
}
