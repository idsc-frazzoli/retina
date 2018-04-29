// code by vc
package ch.ethz.idsc.retina.dev.lidar.app;

import junit.framework.TestCase;

public class TiltedVelodynePlanarEmulatorTest extends TestCase {
  public void testClosest0() {
    TiltedVelodynePlanarEmulator tvpe = new TiltedVelodynePlanarEmulator(0, -0.04, 0);
    assertEquals(tvpe.closestRay(0.1), 1);
    assertEquals(tvpe.closestRay(2.2), 3);
    assertEquals(tvpe.closestRay(1.9), 1);
    assertEquals(tvpe.closestRay(2), 3);
    assertEquals(tvpe.closestRay(-2), -1);
    assertEquals(tvpe.closestRay(-2.1), -3);
    assertEquals(tvpe.closestRay(-3), -3);
  }

  public void testClosest1() {
    TiltedVelodynePlanarEmulator tvpe = new TiltedVelodynePlanarEmulator(0, -0.04, 1);
    assertEquals(tvpe.closestRay(0), 1);
    assertEquals(tvpe.closestRay(2.2), 3);
    assertEquals(tvpe.closestRay(1.9), 3);
    assertEquals(tvpe.closestRay(2), 3);
    assertEquals(tvpe.closestRay(-2), -1);
  }

  public void testID() {
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(-15), 0);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(-13), 2);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(-11), 4);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(1), 1);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(3), 3);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(15), 0);
  }
}
