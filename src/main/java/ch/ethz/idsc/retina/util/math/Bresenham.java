// code by ynager
package ch.ethz.idsc.retina.util.math;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/** Reference:
 * https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm */
public enum Bresenham {
  ;
  /** @param x0 coordinate of starting point
   * @param y0 coordinate of starting point
   * @param x1 coordinate of final point
   * @param y1 coordinate of final point
   * @return list of points in cell coordinates */
  public static List<Point> line( //
      final int x0, final int y0, //
      final int x1, final int y1) {
    List<Point> line = new LinkedList<>();
    int dx = Math.abs(x1 - x0);
    int dy = Math.abs(y1 - y0);
    int sx = x0 < x1 ? 1 : -1;
    int sy = y0 < y1 ? 1 : -1;
    int err = dx - dy;
    int xi = x0;
    int yi = y0;
    line.add(new Point(xi, yi));
    while (xi != x1 || yi != y1) {
      int e2 = 2 * err;
      if (-dy < e2) {
        err -= dy;
        xi += sx;
      }
      if (e2 < dx) {
        err += dx;
        yi += sy;
      }
      line.add(new Point(xi, yi));
    }
    return line;
  }
}
