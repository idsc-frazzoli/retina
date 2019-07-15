// code by am
package ch.ethz.idsc.gokart.core.adas;

import junit.framework.TestCase;

public class LaneKeepingSlowDownModuleTest extends TestCase {
  public void testSimple() {
    LaneKeepingSlowDownModule laneKeepingSlowDownModule = new LaneKeepingSlowDownModule();
    laneKeepingSlowDownModule.first();
    laneKeepingSlowDownModule.putEvent();
    laneKeepingSlowDownModule.last();
    System.out.println(" ");
  }
}
