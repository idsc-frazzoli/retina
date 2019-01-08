// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.owl.math.region.PolygonArea;
import ch.ethz.idsc.owl.math.region.Polygons;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum StaticHelper {
  ;
  public static double computeBetterArea(Tensor polygon) {
    return PolygonArea.FUNCTION.apply(polygon).abs().number().doubleValue();
  }

  // basic performance measure: compute the fraction of predicted centres of clusters that are
  // in the convexHull of the new lidar scan clusters
  public static double evaluatePerformance(Tensor predictedMeans, Tensor hulls) {
    int count = 0;
    for (Tensor z : predictedMeans) {
      for (Tensor hull : hulls) {
        int i = Polygons.isInside(hull, z) ? 1 : 0;
        count += i;
      }
    }
    return count / (double) predictedMeans.length();
  }
}
