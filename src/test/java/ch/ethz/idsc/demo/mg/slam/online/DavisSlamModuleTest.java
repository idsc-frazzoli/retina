package ch.ethz.idsc.demo.mg.slam.online;

import junit.framework.TestCase;

public class DavisSlamModuleTest extends TestCase {
  public void testSimpleNoPose() throws Exception {
    DavisSlamModule davisSlamModule = new DavisSlamModule();
    davisSlamModule.launch();
    Thread.sleep(1000);
    //
    davisSlamModule.terminate();
  }
}
