package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class InititialGuessTest extends TestCase {
  public void testSimple() {
    System.out.println("test");
    TestOccupancyGrid testOccupancyGrid = new TestOccupancyGrid();
    int x = 180;
    int y = 190;
    double so = Math.PI*0.5;
    TrackLayoutInitialGuess initialGuess = new TrackLayoutInitialGuess(testOccupancyGrid);
    long startTime = System.currentTimeMillis();
    initialGuess.update(x, y, so);
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    System.out.println(elapsedTime);
    System.out.println(initialGuess.getRoutePolygon());
    Scalar spacing = RealScalar.of(0.1);
    Scalar controlPointResolution = RealScalar.of(0.5);//half as many control points
    Tensor ig = initialGuess.getControlPointGuess(spacing, controlPointResolution);
    //initialGuess.getRefinedTrack(ig.get(0), ig.get(1), 2);
    
  }
}
