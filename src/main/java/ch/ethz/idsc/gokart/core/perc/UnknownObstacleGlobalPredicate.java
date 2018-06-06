// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

/** the purpose of the class is filter lidar points that are not already in the static obstacle map */
public class UnknownObstacleGlobalPredicate implements SpacialObstaclePredicate {
  private static final int NON_BLACK_MASK = 0xff00;
  // ---
  private final PredefinedMap predefinedMap;
  private final GeometricLayer geometricLayer;

  public UnknownObstacleGlobalPredicate(PredefinedMap predefineMap) {
    this.predefinedMap = predefineMap;
    geometricLayer = GeometricLayer.of(predefinedMap.getModel2Pixel());
  }

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(Tensor point) {
    Point2D point2d = geometricLayer.toPoint2D(point);
    int rgb = predefinedMap.getRGB(point2d);
    return (rgb & NON_BLACK_MASK) != NON_BLACK_MASK;
  }
}
