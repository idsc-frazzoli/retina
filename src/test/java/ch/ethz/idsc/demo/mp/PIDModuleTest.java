package ch.ethz.idsc.demo.mp;

import ch.ethz.idsc.demo.mp.pid.PIDTestTrackModule;
import junit.framework.TestCase;

public class PIDModuleTest extends TestCase {
  public void testFtdstgfs() {
    PIDTestTrackModule pidTestTrackModule = new PIDTestTrackModule();
    pidTestTrackModule.first();
    pidTestTrackModule.last();
  }
}
