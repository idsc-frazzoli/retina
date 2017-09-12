// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import junit.framework.TestCase;

public class DavisTallyEventProviderTest extends TestCase {
  public void testSimple() {
    DavisTallyEventProvider davisEventTally = new DavisTallyEventProvider();
    assertFalse(davisEventTally.isActive());
  }
}
