// code by ynager
// https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
package ch.ethz.idsc.retina.util.math;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public enum Bresenham {
  ;
  /** @param x0 coordinate of starting point
   * @param y0 coordinate of starting point
   * @param x1 coordinate of final point
   * @param y1 coordinate of final point
   * @return list of points in cell coordinates */
  public static List<Point> line(int x0, int y0, int x1, int y1) {
    List<Point> line = new ArrayList<>();
    int dx = Math.abs(x1 - x0);
    int dy = Math.abs(y1 - y0);
    int sx = x0 < x1 ? 1 : -1;
    int sy = y0 < y1 ? 1 : -1;
    int err = dx - dy;
    int xi = x0;
    int yi = y0;
    while (true) {
      line.add(new Point(xi, yi));
      if (xi == x1 && yi == y1)
        break;
      int e2 = 2 * err;
      if (e2 > -dy) {
        err -= dy;
        xi += sx;
      }
      if (e2 < dx) {
        err += dx;
        yi += sy;
      }
    }
    return line;
  }
}
