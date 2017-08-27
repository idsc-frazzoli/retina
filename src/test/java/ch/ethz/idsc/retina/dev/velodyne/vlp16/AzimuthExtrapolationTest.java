// code by jph
package ch.ethz.idsc.retina.dev.velodyne.vlp16;

import junit.framework.TestCase;

public class AzimuthExtrapolationTest extends TestCase {
  public void testSimple() {
    AzimuthExtrapolation ae = new AzimuthExtrapolation();
    try {
      ae.gap();
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    ae.now(10000);
    ae.now(20000);
    ae.now(20000);
    ae.now(20000);
    assertEquals(ae.gap(), 25000);
    ae.now(30000);
    ae.now(30000);
    assertEquals(ae.gap(), 35000);
    ae.now(4000);
    assertEquals(ae.gap(), 9000);
    ae.now(4000);
    assertEquals(ae.gap(), 9000);
    ae.now(4000);
    assertEquals(ae.gap(), 9000);
  }

  public void testSample() {
    int[] data = new int[] { 8480, 8520, 8560, 8600, 8638 };
    AzimuthExtrapolation ae = new AzimuthExtrapolation();
    for (int c = 0; c < data.length; ++c) {
      // System.out.println(data[c]);
      ae.now(data[c]);
      // System.out.println(ae.gap());
    }
  }
}
