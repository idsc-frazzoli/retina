// code by jph
package ch.ethz.idsc.gokart.core.map;

import junit.framework.TestCase;

public class GenericBayesianMappingTest extends TestCase {
  public void testSimple() {
    AbstractMapping<BayesianOccupancyGrid> gokartMappingModule = MappingConfig.GLOBAL.createObstacleMapping();
    gokartMappingModule.start();
    gokartMappingModule.stop();
  }
}
