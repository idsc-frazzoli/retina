// code by jph
package ch.ethz.idsc.gokart.gui;

import junit.framework.TestCase;

public class PanoramaViewModuleTest extends TestCase {
  public void testSimple() throws Exception {
    PanoramaViewModule pvm = new PanoramaViewModule();
    pvm.first();
    Thread.sleep(200);
    pvm.last();
  }
}
