// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx;

import junit.framework.TestCase;

public class Urg04lxLiveProviderTest extends TestCase {
  public void testSimple() {
    assertTrue(Urg04lxLiveProvider.EXECUTABLE.isAbsolute());
  }
}
