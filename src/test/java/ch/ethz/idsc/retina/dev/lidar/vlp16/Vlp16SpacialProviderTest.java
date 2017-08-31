// code by jph
package ch.ethz.idsc.retina.dev.lidar.vlp16;

import junit.framework.TestCase;

public class Vlp16SpacialProviderTest extends TestCase {
  public void testDegree() {
    assertEquals(Vlp16SpacialProvider.degree(0), -15);
    assertEquals(Vlp16SpacialProvider.degree(1), 1);
    assertEquals(Vlp16SpacialProvider.degree(2), -13);
    assertEquals(Vlp16SpacialProvider.degree(3), 3);
    assertEquals(Vlp16SpacialProvider.degree(4), -11);
    assertEquals(Vlp16SpacialProvider.degree(5), 5);
    assertEquals(Vlp16SpacialProvider.degree(6), -9);
    assertEquals(Vlp16SpacialProvider.degree(7), 7);
    assertEquals(Vlp16SpacialProvider.degree(8), -7);
    assertEquals(Vlp16SpacialProvider.degree(9), 9);
    assertEquals(Vlp16SpacialProvider.degree(10), -5);
    assertEquals(Vlp16SpacialProvider.degree(11), 11);
    assertEquals(Vlp16SpacialProvider.degree(12), -3);
    assertEquals(Vlp16SpacialProvider.degree(13), 13);
    assertEquals(Vlp16SpacialProvider.degree(14), -1);
    assertEquals(Vlp16SpacialProvider.degree(15), 15);
  }
}
