// code by am
package ch.ethz.idsc.gokart.core.adas;

import junit.framework.TestCase;

public class LaneKeepingLimitedSteeringModuleTest extends TestCase {
  public void testSimple3() {
    LaneKeepingSlowDownModule laneKeepingSlowDownModule = new LaneKeepingSlowDownModule();
    laneKeepingSlowDownModule.first();
    laneKeepingSlowDownModule.putEvent();
    laneKeepingSlowDownModule.last();
    System.out.println(" ");
  }

  public void testSimple6() {
    LaneKeepingLimitedSteeringModule laneKeepingLimitedSteeringModule = new LaneKeepingLimitedSteeringModule();
    laneKeepingLimitedSteeringModule.first();
    assertFalse(laneKeepingLimitedSteeringModule.putEvent().isPresent());
    laneKeepingLimitedSteeringModule.last();
    System.out.println(" ");
  }
}
