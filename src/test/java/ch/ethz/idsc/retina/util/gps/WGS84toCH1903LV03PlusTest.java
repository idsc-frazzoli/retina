// code by jph
package ch.ethz.idsc.retina.util.gps;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class WGS84toCH1903LV03PlusTest extends TestCase {
  // Basel has
  // x="2678402.0" y="1254426.0"
  // x="2765986.0" y="1303770.0"
  public void testSimple() {
    Scalar coord_getX = Quantity.of(8.3786, "deg");
    Scalar coord_getY = Quantity.of(47.2434, "deg");
    Tensor metric = WGS84toCH1903LV03Plus.transform(coord_getX, coord_getY);
    Tensor gps = CH1903LV03PlustoWGS84.transform(metric.Get(0), metric.Get(1));
    Tensor ori = Tensors.of(coord_getX, coord_getY);
    Scalar diff = Norm._2.between(ori, gps);
    assertTrue(Scalars.lessThan(diff, Quantity.of(1E-5, "deg")));
  }
}
