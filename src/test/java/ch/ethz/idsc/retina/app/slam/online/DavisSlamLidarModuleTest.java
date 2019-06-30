// code by mg
package ch.ethz.idsc.retina.app.slam.online;

import junit.framework.TestCase;

public class DavisSlamLidarModuleTest extends TestCase {
  public void testSimpleNoPose() throws Exception {
    DavisSlamLidarModule davisSlamLidarModule = new DavisSlamLidarModule();
    davisSlamLidarModule.launch();
    Thread.sleep(1000);
    //
    davisSlamLidarModule.terminate();
  }
}
