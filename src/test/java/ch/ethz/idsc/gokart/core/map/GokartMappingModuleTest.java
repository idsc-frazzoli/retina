// code by jph
package ch.ethz.idsc.gokart.core.map;

import junit.framework.TestCase;

public class GokartMappingModuleTest extends TestCase {
  public void testSimple() {
    GokartMappingModule gokartMappingModule = new GokartMappingModule();
    gokartMappingModule.start();
    gokartMappingModule.stop();
  }
}
