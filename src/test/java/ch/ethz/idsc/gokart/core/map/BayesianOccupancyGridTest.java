// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BayesianOccupancyGridTest extends TestCase {
  public void testConstruct() {
    Tensor GRID_RANGE = Tensors.vector(40, 40); // TODO comment on magic const 640/7.5
    Tensor LOWER_BOUND = Tensors.vector(30, 30);
    BayesianOccupancyGrid.of(LOWER_BOUND, GRID_RANGE, MappingConfig.GLOBAL.cellDim);
  }
}
