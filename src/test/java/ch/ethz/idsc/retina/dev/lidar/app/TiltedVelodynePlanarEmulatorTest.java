// code by vc
package ch.ethz.idsc.retina.dev.lidar.app;

import junit.framework.TestCase;

public class TiltedVelodynePlanarEmulatorTest extends TestCase {
  public void testClosest() {
    assertEquals(TiltedVelodynePlanarEmulator.closestRay(0), 1);
    assertEquals(TiltedVelodynePlanarEmulator.closestRay(2.2), 3);
    assertEquals(TiltedVelodynePlanarEmulator.closestRay(1.9), 3);
    assertEquals(TiltedVelodynePlanarEmulator.closestRay(2), 3);
    assertEquals(TiltedVelodynePlanarEmulator.closestRay(-2), -1);
  }

  public void testID() {
    // for (int deg = -15; deg < 16; deg += 2)
    // System.out.println(deg + " " + TiltedVelodynePlanarEmulator.degreeToLidarID(deg));
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(-15), 0);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(-13), 2);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(-11), 4);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(1), 1);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(3), 3);
    assertEquals(TiltedVelodynePlanarEmulator.degreeToLidarID(15), 0);
  }
}
