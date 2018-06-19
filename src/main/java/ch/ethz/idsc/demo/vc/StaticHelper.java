// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.owl.math.planar.PolygonArea;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum StaticHelper {
  ;
  public static double computeBetterArea(Tensor polygon) {
    return PolygonArea.FUNCTION.apply(polygon).abs().number().doubleValue();
  }
}
