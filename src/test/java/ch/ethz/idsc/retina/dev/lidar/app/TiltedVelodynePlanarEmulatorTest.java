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
    // TODO: change to assert
    for (int deg = -15; deg < 16; deg += 2)
      System.out.println(deg + " " + TiltedVelodynePlanarEmulator.degreeToLidarID(deg));
  }
}
