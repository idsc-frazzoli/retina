// code by jph
package ch.ethz.idsc.gokart.core.map;

import junit.framework.TestCase;

public class GokartMappingModuleTest extends TestCase {
  public void testSimple() {
    ObstacleMapping gokartMappingModule = new ObstacleMapping();
    gokartMappingModule.start();
    gokartMappingModule.stop();
  }
}
