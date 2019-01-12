// code by jph
package ch.ethz.idsc.retina.davis.app;

import junit.framework.TestCase;

public class ExpDecayLookupTest extends TestCase {
  public void testSimpleP() {
    int n = 10;
    ExpDecayLookup edl = new ExpDecayLookup(n, 3.0, +1);
    int last = 128;
    for (int delta = 0; delta < n; ++delta) {
      int value = edl.get(delta) & 0xff;
      assertTrue(last < value);
      last = value;
    }
  }

  public void testSimpleN() {
    int n = 10;
    ExpDecayLookup edl = new ExpDecayLookup(n, 3.0, -1);
    int last = 128;
    for (int delta = 0; delta < n; ++delta) {
      int value = edl.get(delta) & 0xff;
      assertTrue(value < last);
      last = value;
    }
  }
}
