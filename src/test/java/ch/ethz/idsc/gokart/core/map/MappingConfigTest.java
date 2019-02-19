// code by jph
package ch.ethz.idsc.gokart.core.map;

import junit.framework.TestCase;

public class MappingConfigTest extends TestCase {
  public void testConstruct() {
    MappingConfig.GLOBAL.createBayesianOccupancyGrid();
  }
}
