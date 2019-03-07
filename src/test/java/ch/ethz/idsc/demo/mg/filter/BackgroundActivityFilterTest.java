// code by jph
package ch.ethz.idsc.demo.mg.filter;

import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import junit.framework.TestCase;

public class BackgroundActivityFilterTest extends TestCase {
  public void testSimple() {
    BackgroundActivityFilter backgroundActivityFilter = new BackgroundActivityFilter(80, 30, 1000);
    assertFalse(backgroundActivityFilter.filter(new DavisDvsEvent(2000, 3, 4, 1)));
    assertFalse(backgroundActivityFilter.filter(new DavisDvsEvent(2001, 3, 4, 1)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2002, 4, 5, 0)));
    assertFalse(backgroundActivityFilter.filter(new DavisDvsEvent(4002, 4, 5, 0)));
  }

  public void testBorder() {
    BackgroundActivityFilter backgroundActivityFilter = new BackgroundActivityFilter(80, 30, 1000);
    assertFalse(backgroundActivityFilter.filter(new DavisDvsEvent(2000, 0, 0, 1)));
    assertFalse(backgroundActivityFilter.filter(new DavisDvsEvent(2001, 1, 1, 1)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2001, 2, 2, 0)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2002, 2, 2, 0)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2003, 1, 1, 1)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2004, 0, 0, 0)));
  }

  public void testLowerRight() {
    BackgroundActivityFilter backgroundActivityFilter = new BackgroundActivityFilter(10, 5, 1000);
    assertFalse(backgroundActivityFilter.filter(new DavisDvsEvent(2000, 9, 4, 1)));
    assertFalse(backgroundActivityFilter.filter(new DavisDvsEvent(2001, 8, 3, 1)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2001, 7, 2, 0)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2002, 7, 2, 0)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2003, 8, 3, 1)));
    assertTrue(backgroundActivityFilter.filter(new DavisDvsEvent(2004, 9, 4, 0)));
  }
}
