// code by mh
package ch.ethz.idsc.gokart.core.track;

import java.awt.image.BufferedImage;
import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.Timing;
import junit.framework.TestCase;

public class TrackLayoutInitialGuessTest extends TestCase {
  public void testSimple() {
    BufferedImage bufferedImage = ResourceData.bufferedImage("/map/occupancygrid/small.png");
    TestOccupancyGrid testOccupancyGrid = new TestOccupancyGrid(bufferedImage);
    int x = 180 / 4;
    int y = 190 / 4;
    double so = Math.PI * 0.5;
    TrackLayoutInitialGuess trackLayoutInitialGuess = new TrackLayoutInitialGuess(testOccupancyGrid);
    Timing timing = Timing.started();
    trackLayoutInitialGuess.update(x, y, so);
    long elapsedTime = timing.nanoSeconds();
    System.out.println(elapsedTime + "[ns]");
    Optional<Tensor> routePolygon = trackLayoutInitialGuess.getRoutePolygon();
    assertTrue(routePolygon.isPresent());
    MatrixQ.require(routePolygon.get());
    // System.out.println();
    Scalar spacing = RealScalar.of(1.5);
    Scalar controlPointResolution = RealScalar.of(0.5);// half as many control points
    Optional<Tensor> optional = trackLayoutInitialGuess.getControlPointGuess(spacing, controlPointResolution);
    assertTrue(optional.isPresent());
    // initialGuess.getRefinedTrack(ig.get(0), ig.get(1), 2);
  }
}
