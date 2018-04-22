// code by jph
package ch.ethz.idsc.gokart.gui;

import junit.framework.TestCase;

public class ParametersModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ParametersModule parametersModule = new ParametersModule();
    parametersModule.first();
    Thread.sleep(200);
    parametersModule.last();
  }
}
