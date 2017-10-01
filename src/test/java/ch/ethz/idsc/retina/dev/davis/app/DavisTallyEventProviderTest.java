// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import junit.framework.TestCase;

public class DavisTallyEventProviderTest extends TestCase {
  public void testSimple() {
    DavisTallyProvider davisEventTally = new DavisTallyProvider(null);
    assertFalse(davisEventTally.isTriggered());
  }
}
