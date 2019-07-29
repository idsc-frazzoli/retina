// code by jph, mg
package ch.ethz.idsc.retina.app.slam.online;

import junit.framework.TestCase;

public class DavisSlamVisualModuleTest extends TestCase {
  public void testSimpleNoPose() throws Exception {
    DavisSlamVisualModule davisSlamVisualModule = new DavisSlamVisualModule();
    davisSlamVisualModule.launch();
    Thread.sleep(1000);
    //
    davisSlamVisualModule.terminate();
  }
}
