// code by jph
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import junit.framework.TestCase;

public class AccumulatedEventsGrayImageTest extends TestCase {
  public void testSimple() {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    AbstractAccumulatedImage abstractAccumulatedImage = AccumulatedEventsGrayImage.of(davisDevice);
    abstractAccumulatedImage.assign(1, new DavisDvsEvent(1, 2, 3, 1));
    abstractAccumulatedImage.assign(1, new DavisDvsEvent(1, 4, 13, 0));
    assertEquals(abstractAccumulatedImage.bytes[0], -128); // gray
    assertEquals(abstractAccumulatedImage.bytes[2 + 3 * 240], -1); // white
    assertEquals(abstractAccumulatedImage.bytes[4 + 13 * 240], 0); // black
  }

  public void testEvent() {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    AbstractAccumulatedImage abstractAccumulatedImage = AccumulatedEventsGrayImage.of(davisDevice);
    abstractAccumulatedImage.davisDvs(new DavisDvsEvent(1, 2, 3, 1));
    abstractAccumulatedImage.davisDvs(new DavisDvsEvent(2, 4, 13, 0));
    abstractAccumulatedImage.davisDvs(new DavisDvsEvent(3, 239, 179, 1));
    assertEquals(abstractAccumulatedImage.bytes[0], -128); // gray
    assertEquals(abstractAccumulatedImage.bytes[2 + 3 * 240], -1); // white
    assertEquals(abstractAccumulatedImage.bytes[4 + 13 * 240], 0); // black
    assertEquals(abstractAccumulatedImage.bytes[239 + 179 * 240], -1); // white
  }
}
