package ch.ethz.idsc.retina.util.math;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Bresenham {
  /** @param x0 x coordinate of starting point
   * @param y0 y coordinate of starting point
   * @param x1 x coordinate of final point
   * @param y1 y coordinate of final point
   * @return List of points in cell coordinates */
  public static List<Point> getLine(int x0, int y0, int x1, int y1) {
    List<Point> line = new ArrayList<>();
    int dx = Math.abs(x1 - x0);
    int dy = Math.abs(y1 - y0);
    int sx = x0 < x1 ? 1 : -1;
    int sy = y0 < y1 ? 1 : -1;
    int err = dx - dy;
    int e2;
    int currentX = x0;
    int currentY = y0;
    while (true) {
      line.add(new Point(currentX, currentY));
      if (currentX == x1 && currentY == y1) {
        break;
      }
      e2 = 2 * err;
      if (e2 > -1 * dy) {
        err = err - dy;
        currentX = currentX + sx;
      }
      if (e2 < dx) {
        err = err + dx;
        currentY = currentY + sy;
      }
    }
    return line;
  }
}
