// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import ch.ethz.idsc.retina.dev.davis.DavisApsType;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import junit.framework.TestCase;

public class DavisApsBlockPublisherTest extends TestCase {
  public void testSimple() {
    assertEquals( //
        DavisApsBlockPublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW, DavisApsType.SIG), //
        "davis240c.overview.sig");
    assertEquals( //
        DavisApsBlockPublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW, DavisApsType.RST), //
        "davis240c.overview.rst");
  }
}
