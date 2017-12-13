// code by jph
package ch.ethz.idsc.retina.util.gps;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class WGS84toCH1903LV03PlusTest extends TestCase {
  // Basel has
  // x="2678402.0" y="1254426.0"
  // x="2765986.0" y="1303770.0"
  public void testSimple() {
    double coord_getX = 8.3786;
    double coord_getY = 47.2434;
    Tensor metric = WGS84toCH1903LV03Plus.transform(coord_getX, coord_getY);
    // System.out.println(metric);
    Tensor gps = CH1903LV03PlustoWGS84.transform( //
        metric.Get(0).number().doubleValue(), //
        metric.Get(1).number().doubleValue());
    Tensor ori = Tensors.vector(coord_getX, coord_getY);
    Scalar diff = Norm._2.between(ori, gps);
    assertTrue(Scalars.lessThan(diff, RealScalar.ONE));
  }

  public void testInversion() {
    final double coord_getX = 2678402.0;
    final double coord_getY = 1254426.0;
    Tensor gps = CH1903LV03PlustoWGS84.transform(coord_getX, coord_getY);
    // System.out.println(gps);
    double coord_getX1 = gps.Get(0).number().doubleValue();
    double coord_getY1 = gps.Get(1).number().doubleValue();
    Tensor metric = WGS84toCH1903LV03Plus.transform(coord_getX1, coord_getY1);
    // System.out.println(metric);
    Tensor ori = Tensors.vector(coord_getX, coord_getY);
    Scalar diff = Norm._2.between(ori, metric);
    // System.out.println(diff);
    assertTrue(Scalars.lessThan(diff, RealScalar.ONE));
  }
}
