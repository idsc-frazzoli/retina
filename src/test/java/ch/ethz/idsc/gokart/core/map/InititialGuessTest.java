// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import junit.framework.TestCase;

public class InititialGuessTest extends TestCase {
  public void testSimple() throws IOException {
    System.out.println("test");
    File file = HomeDirectory.file("Documents/bigOccupancyGrid.png");
    if (file.isFile()) {
      BufferedImage img = ImageIO.read(file);
      TestOccupancyGrid testOccupancyGrid = new TestOccupancyGrid(img);
      int x = 180;
      int y = 190;
      double so = Math.PI * 0.5;
      TrackLayoutInitialGuess initialGuess = new TrackLayoutInitialGuess(testOccupancyGrid);
      long startTime = System.currentTimeMillis();
      initialGuess.update(x, y, so);
      long stopTime = System.currentTimeMillis();
      long elapsedTime = stopTime - startTime;
      System.out.println(elapsedTime);
      System.out.println(initialGuess.getRoutePolygon());
      Scalar spacing = RealScalar.of(0.1);
      Scalar controlPointResolution = RealScalar.of(0.5);// half as many control points
      Tensor ig = initialGuess.getControlPointGuess(spacing, controlPointResolution);
      // initialGuess.getRefinedTrack(ig.get(0), ig.get(1), 2);
    } else {
      System.err.println("skip test: image not available");
    }
  }
}
