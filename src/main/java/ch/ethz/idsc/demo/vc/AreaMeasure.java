// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/** approximates measure of {@link Area} by uniformly sampling points and counting containment */
/* package */ enum AreaMeasure {
  ;
  /** resolution */
  private static final int RES = 300;

  /** @param area
   * @return */
  public static double of(Area area) {
    int count = 0;
    Rectangle2D bounds2d = area.getBounds2D();
    double x = bounds2d.getX();
    double y = bounds2d.getY();
    double width = bounds2d.getWidth();
    double height = bounds2d.getHeight();
    for (int i = 0; i < RES; i++) {
      for (int j = 0; j < RES; j++) {
        Point2D point = new Point2D.Double(x + i * width / RES, y + j * height / RES);
        if (area.contains(point))
          ++count;
      }
    }
    return height * width * count / (RES * RES);
  }

  public static double midpoint(Area area) {
    int count = 0;
    Rectangle2D bounds2d = area.getBounds2D();
    double x = bounds2d.getX();
    double y = bounds2d.getY();
    double width = bounds2d.getWidth();
    double height = bounds2d.getHeight();
    // TODO VC adapt resolution depending on width and height
    double dx = bounds2d.getWidth() / RES;
    double dy = bounds2d.getHeight() / RES;
    double mx = x + width;
    double my = y + height;
    for (double ix = x + dx / 2; ix < mx; ix += dx) {
      for (double iy = y + dy / 2; iy < my; iy += dy) {
        Point2D point = new Point2D.Double(ix, iy);
        if (area.contains(point))
          ++count;
      }
    }
    return height * width * count / (RES * RES);
  }
}
