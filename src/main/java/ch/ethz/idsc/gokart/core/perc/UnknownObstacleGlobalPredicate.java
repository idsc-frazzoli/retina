// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

/** the purpose of the class is filter lidar points that are
 * 1) NOT already in the static obstacle map
 * 2) NOT floor */
public class UnknownObstacleGlobalPredicate implements SpacialObstaclePredicate {
  private static final int NON_BLACK_MASK = 0xff00;
  GeometricLayer geometricLayer;
  // ---
  private final PredefinedMap predefinedMap;

  public UnknownObstacleGlobalPredicate(PredefinedMap predefineMap) {
    this.predefinedMap = predefineMap;
    geometricLayer = GeometricLayer.of(predefinedMap.getModel2Pixel());
  }

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(Tensor point) {
    Point2D point2d = geometricLayer.toPoint2D(point);
    // new Point2D.Double(//
    // point.Get(0).number().doubleValue(), //
    // point.Get(1).number().doubleValue());
    int rgb = predefinedMap.getRGB(point2d);
    if ((rgb & NON_BLACK_MASK) == NON_BLACK_MASK)
      return false;
    return true;
  }
}
