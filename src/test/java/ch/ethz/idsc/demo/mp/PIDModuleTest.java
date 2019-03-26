//code by mcp
package ch.ethz.idsc.demo.mp;

import ch.ethz.idsc.demo.mp.pid.PIDTestTrackModule;
import junit.framework.TestCase;

public class PIDModuleTest extends TestCase {
  public void testBaseline() {
    PIDTestTrackModule pidTestTrackModule = new PIDTestTrackModule();
    pidTestTrackModule.first();
    pidTestTrackModule.last();
  }
}
