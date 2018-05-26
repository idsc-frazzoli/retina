// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Tensor;

// inspired of https://www.mathopenref.com/coordpolygonarea.html
public enum PolygonArea {
  ;
  /** @param polygon not necessarily convex
   * @return signed area circumscribed by given polygon,
   * area is positive when polygon is in counter-clockwise direction */
  public static double signed(Tensor polygon) {
    int last = polygon.length() - 1;
    double intermediate = PolygonArea.det(polygon.get(last), polygon.get(0));
    // polygon.Get(last, 0).number().doubleValue() * polygon.Get(0, 1).number().doubleValue()
    // - polygon.Get(0, 0).number().doubleValue() * polygon.Get(last, 1).number().doubleValue();
    for (int i = 0; i < last; ++i)
      intermediate += PolygonArea.det(polygon.get(i), polygon.get(i + 1));
    // polygon.Get(i, 0).number().doubleValue() * polygon.Get(i + 1, 1).number().doubleValue()
    // - polygon.Get(i, 1).number().doubleValue() * polygon.Get(i + 1, 0).number().doubleValue();
    return intermediate / 2;
  }

  // helper function
  private static double det(Tensor p, Tensor q) {
    // return q.dot(Cross2D.of(p)).Get().number().doubleValue(); // alternative version
    return p.Get(0).number().doubleValue() * q.Get(1).number().doubleValue() //
        - q.Get(0).number().doubleValue() * p.Get(1).number().doubleValue();
  }
}
