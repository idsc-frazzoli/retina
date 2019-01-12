// code by vc
package ch.ethz.idsc.retina.lidar.vlp16;

import junit.framework.TestCase;

public class Vlp16RayLookupTest extends TestCase {
  public void test18000() {
    new Vlp16RayLookup(1, true, 0, -0.04, 0);
  }

  public void testClosest0() {
    Vlp16RayLookup tvpe = new Vlp16RayLookup(0, true, 0, -0.04, 0);
    assertEquals(tvpe.closestRay(0.1), 1);
    assertEquals(tvpe.closestRay(2.2), 3);
    assertEquals(tvpe.closestRay(1.9), 1);
    assertEquals(tvpe.closestRay(2), 3);
    assertEquals(tvpe.closestRay(-2), -1);
    assertEquals(tvpe.closestRay(-2.1), -3);
    assertEquals(tvpe.closestRay(-3), -3);
  }

  public void testClosest1() {
    Vlp16RayLookup tvpe = new Vlp16RayLookup(0, true, 0, -0.04, 1);
    // TiltedVelodynePlanarEmulator tvpe = new TiltedVelodynePlanarEmulator(0, -0.04, 1);
    assertEquals(tvpe.closestRay(0), 1);
    assertEquals(tvpe.closestRay(2.2), 3);
    assertEquals(tvpe.closestRay(1.9), 3);
    assertEquals(tvpe.closestRay(2), 3);
    assertEquals(tvpe.closestRay(-2), -1);
  }

  public void testID() {
    assertEquals(Vlp16RayLookup.degreeToLidarID(-15), 0);
    assertEquals(Vlp16RayLookup.degreeToLidarID(-13), 2);
    assertEquals(Vlp16RayLookup.degreeToLidarID(-11), 4);
    assertEquals(Vlp16RayLookup.degreeToLidarID(1), 1);
    assertEquals(Vlp16RayLookup.degreeToLidarID(3), 3);
    assertEquals(Vlp16RayLookup.degreeToLidarID(15), 0);
  }
}
