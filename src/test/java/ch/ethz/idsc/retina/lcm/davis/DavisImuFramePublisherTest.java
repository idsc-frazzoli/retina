// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import junit.framework.TestCase;

public class DavisImuFramePublisherTest extends TestCase {
  public void testSimple() {
    String channel = DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
    assertEquals(channel, "davis240c.overview.atg");
  }
}
