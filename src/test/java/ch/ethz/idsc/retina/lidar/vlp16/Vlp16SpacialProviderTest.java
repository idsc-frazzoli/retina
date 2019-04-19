// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import junit.framework.TestCase;

public class Vlp16SpacialProviderTest extends TestCase {
  public void testDegree() {
    assertEquals(Vlp16Helper.degree(0), -15);
    assertEquals(Vlp16Helper.degree(1), 1);
    assertEquals(Vlp16Helper.degree(2), -13);
    assertEquals(Vlp16Helper.degree(3), 3);
    assertEquals(Vlp16Helper.degree(4), -11);
    assertEquals(Vlp16Helper.degree(5), 5);
    assertEquals(Vlp16Helper.degree(6), -9);
    assertEquals(Vlp16Helper.degree(7), 7);
    assertEquals(Vlp16Helper.degree(8), -7);
    assertEquals(Vlp16Helper.degree(9), 9);
    assertEquals(Vlp16Helper.degree(10), -5);
    assertEquals(Vlp16Helper.degree(11), 11);
    assertEquals(Vlp16Helper.degree(12), -3);
    assertEquals(Vlp16Helper.degree(13), 13);
    assertEquals(Vlp16Helper.degree(14), -1);
    assertEquals(Vlp16Helper.degree(15), 15);
  }
}
