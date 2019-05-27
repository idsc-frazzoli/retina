//code by mcp
package ch.ethz.idsc.demo.mp.pid;

import junit.framework.TestCase;

public class PIDTestTrackModuleTest extends TestCase {
  public void testBaseline() {
    PIDTestTrackModule pidTestTrackModule = new PIDTestTrackModule();
    pidTestTrackModule.first();
    pidTestTrackModule.last();
  }
  // TODO Same test functions ad PIDControllerModuleTest
}