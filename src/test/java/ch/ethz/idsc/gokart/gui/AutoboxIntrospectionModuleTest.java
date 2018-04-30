// code by jph
package ch.ethz.idsc.gokart.gui;

import junit.framework.TestCase;

public class AutoboxIntrospectionModuleTest extends TestCase {
  public void testSimple() throws Exception {
    AutoboxIntrospectionModule aim = new AutoboxIntrospectionModule();
    aim.first();
    Thread.sleep(200);
    aim.last();
  }
}
