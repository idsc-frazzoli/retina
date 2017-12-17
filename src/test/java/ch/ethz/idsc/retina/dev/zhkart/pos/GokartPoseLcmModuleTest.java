// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import junit.framework.TestCase;

public class GokartPoseLcmModuleTest extends TestCase {
  public void testSimple() throws Exception {
    GokartPoseLcmModule gplm = new GokartPoseLcmModule();
    gplm.first();
    gplm.runAlgo();
    gplm.last();
  }
}
