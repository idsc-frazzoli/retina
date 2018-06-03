// code by vc
package ch.ethz.idsc.retina.dev.lidar.app;

import junit.framework.TestCase;

public class VelodyneRayLookupTest extends TestCase {
  public void test18000() {
    new VelodyneRayLookup(1, true, 0, -0.04, 0);
  }

  public void testClosest0() {
    VelodyneRayLookup tvpe = new VelodyneRayLookup(0, true, 0, -0.04, 0);
    assertEquals(tvpe.closestRay(0.1), 1);
    assertEquals(tvpe.closestRay(2.2), 3);
    assertEquals(tvpe.closestRay(1.9), 1);
    assertEquals(tvpe.closestRay(2), 3);
    assertEquals(tvpe.closestRay(-2), -1);
    assertEquals(tvpe.closestRay(-2.1), -3);
    assertEquals(tvpe.closestRay(-3), -3);
  }

  public void testClosest1() {
    VelodyneRayLookup tvpe = new VelodyneRayLookup(0, true, 0, -0.04, 1);
    // TiltedVelodynePlanarEmulator tvpe = new TiltedVelodynePlanarEmulator(0, -0.04, 1);
    assertEquals(tvpe.closestRay(0), 1);
    assertEquals(tvpe.closestRay(2.2), 3);
    assertEquals(tvpe.closestRay(1.9), 3);
    assertEquals(tvpe.closestRay(2), 3);
    assertEquals(tvpe.closestRay(-2), -1);
  }

  public void testID() {
    assertEquals(VelodyneRayLookup.degreeToLidarID(-15), 0);
    assertEquals(VelodyneRayLookup.degreeToLidarID(-13), 2);
    assertEquals(VelodyneRayLookup.degreeToLidarID(-11), 4);
    assertEquals(VelodyneRayLookup.degreeToLidarID(1), 1);
    assertEquals(VelodyneRayLookup.degreeToLidarID(3), 3);
    assertEquals(VelodyneRayLookup.degreeToLidarID(15), 0);
  }
}
