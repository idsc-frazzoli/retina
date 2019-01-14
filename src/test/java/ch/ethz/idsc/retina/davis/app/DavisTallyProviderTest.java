// code by jph
package ch.ethz.idsc.retina.davis.app;

import junit.framework.TestCase;

public class DavisTallyProviderTest extends TestCase {
  public void testSimple() {
    DavisTallyProvider davisEventTally = new DavisTallyProvider(null);
    assertFalse(davisEventTally.isTriggered());
  }
}
