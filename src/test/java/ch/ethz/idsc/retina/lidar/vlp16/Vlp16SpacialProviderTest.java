// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import junit.framework.TestCase;

public class Vlp16SpacialProviderTest extends TestCase {
  public void testDegree() {
    assertEquals(StaticHelper.degree(0), -15);
    assertEquals(StaticHelper.degree(1), 1);
    assertEquals(StaticHelper.degree(2), -13);
    assertEquals(StaticHelper.degree(3), 3);
    assertEquals(StaticHelper.degree(4), -11);
    assertEquals(StaticHelper.degree(5), 5);
    assertEquals(StaticHelper.degree(6), -9);
    assertEquals(StaticHelper.degree(7), 7);
    assertEquals(StaticHelper.degree(8), -7);
    assertEquals(StaticHelper.degree(9), 9);
    assertEquals(StaticHelper.degree(10), -5);
    assertEquals(StaticHelper.degree(11), 11);
    assertEquals(StaticHelper.degree(12), -3);
    assertEquals(StaticHelper.degree(13), 13);
    assertEquals(StaticHelper.degree(14), -1);
    assertEquals(StaticHelper.degree(15), 15);
  }
}
