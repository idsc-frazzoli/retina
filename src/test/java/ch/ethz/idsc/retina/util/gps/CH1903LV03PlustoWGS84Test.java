// code by jph
package ch.ethz.idsc.retina.util.gps;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class CH1903LV03PlustoWGS84Test extends TestCase {
  public void testInversion() {
    final Scalar coord_getX = Quantity.of(2678402.0, "m");
    final Scalar coord_getY = Quantity.of(1254426.0, "m");
    Tensor gps = CH1903LV03PlustoWGS84.transform(coord_getX, coord_getY);
    Scalar coord_getX1 = gps.Get(0);
    Scalar coord_getY1 = gps.Get(1);
    Tensor metric = WGS84toCH1903LV03Plus.transform(coord_getX1, coord_getY1);
    Tensor ori = Tensors.of(coord_getX, coord_getY);
    Scalar diff = Norm._2.between(ori, metric);
    assertTrue(Scalars.lessThan(diff, Quantity.of(1, "m")));
  }

  public void testInversionKm() {
    final Scalar coord_getX = Quantity.of(2678.402, "km");
    final Scalar coord_getY = Quantity.of(1254.426, "km");
    Tensor gps = CH1903LV03PlustoWGS84.transform(coord_getX, coord_getY);
    Scalar coord_getX1 = gps.Get(0);
    Scalar coord_getY1 = gps.Get(1);
    Tensor metric = WGS84toCH1903LV03Plus.transform(coord_getX1, coord_getY1);
    Tensor ori = Tensors.of(coord_getX, coord_getY).map(UnitSystem.SI());
    Scalar diff = Norm._2.between(ori, metric);
    assertTrue(Scalars.lessThan(diff, Quantity.of(1, "m")));
  }
}
