// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class MappingConfigTest extends TestCase {
  public void testConstruct() {
    MappingConfig.GLOBAL.createBayesianOccupancyGrid();
  }

  public void testLambda() {
    Clips.unit().requireInside(MappingConfig.GLOBAL.lambda);
  }
}
