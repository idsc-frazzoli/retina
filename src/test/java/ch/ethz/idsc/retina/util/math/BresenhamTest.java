// code by ynager
package ch.ethz.idsc.retina.util.math;

import java.awt.Point;
import java.util.List;

import junit.framework.TestCase;

public class BresenhamTest extends TestCase {
  public void testEnds() {
    List<Point> line = Bresenham.line(3, 4, 11, 10);
    assertEquals(line.size(), 9);
    assertEquals(line.get(0).x, 3);
    assertEquals(line.get(0).y, 4);
    assertEquals(line.get(line.size() - 1).x, 11);
    assertEquals(line.get(line.size() - 1).y, 10);
  }

  public void testMore() {
    List<Point> line = Bresenham.line(30, 40, 11, 10);
    assertEquals(line.size(), 31);
    assertEquals(line.get(0).x, 30);
    assertEquals(line.get(0).y, 40);
    assertEquals(line.get(line.size() - 1).x, 11);
    assertEquals(line.get(line.size() - 1).y, 10);
  }

  public void testMore2() {
    List<Point> line = Bresenham.line(30, 10, 11, 40);
    assertEquals(line.size(), 31);
    assertEquals(line.get(0).x, 30);
    assertEquals(line.get(0).y, 10);
    assertEquals(line.get(line.size() - 1).x, 11);
    assertEquals(line.get(line.size() - 1).y, 40);
  }

  public void testMore3() {
    List<Point> line = Bresenham.line(11, 40, 30, 10);
    assertEquals(line.size(), 31);
    assertEquals(line.get(0).x, 11);
    assertEquals(line.get(0).y, 40);
    assertEquals(line.get(line.size() - 1).x, 30);
    assertEquals(line.get(line.size() - 1).y, 10);
  }

  public void testSingle() {
    List<Point> line = Bresenham.line(11, 40, 11, 40);
    assertEquals(line.size(), 1);
    assertEquals(line.get(0).x, 11);
    assertEquals(line.get(0).y, 40);
  }
}
