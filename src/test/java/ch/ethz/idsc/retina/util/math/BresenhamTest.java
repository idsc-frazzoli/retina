package ch.ethz.idsc.retina.util.math;

import java.awt.Point;
import java.util.List;

import ch.ethz.idsc.subare.util.GlobalAssert;
import junit.framework.TestCase;

public class BresenhamTest extends TestCase {
  public void testEnds() {
    List<Point> line = Bresenham.getLine(3, 3, 10, 10);
    System.out.print(line);
    GlobalAssert.that(line.get(0).getX() //
    == 3 && line.get(0).getX() == 3);
    GlobalAssert.that(line.get(line.size() - 1).getX() //
    == 10 && line.get(line.size() - 1).getX() == 10);
  }
}
