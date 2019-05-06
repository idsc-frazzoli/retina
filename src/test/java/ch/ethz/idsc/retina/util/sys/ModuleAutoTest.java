// code by jph
package ch.ethz.idsc.retina.util.sys;

import junit.framework.TestCase;

class TestModule extends AbstractModule {
  static int count = 0;

  @Override
  protected void first() {
    count += 10;
  }

  @Override
  protected void last() {
    count += 1;
  }
}

public class ModuleAutoTest extends TestCase {
  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(TestModule.class);
    ModuleAuto.INSTANCE.endAll();
    assertEquals(TestModule.count, 11);
  }
}
