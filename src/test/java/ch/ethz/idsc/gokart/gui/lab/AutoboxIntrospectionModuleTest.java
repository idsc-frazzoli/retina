// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class AutoboxIntrospectionModuleTest extends TestCase {
  public void testSimple() throws Exception {
    AutoboxIntrospectionModule autoboxIntrospectionModule = new AutoboxIntrospectionModule();
    autoboxIntrospectionModule.first();
    Thread.sleep(200);
    autoboxIntrospectionModule.last();
  }
}
