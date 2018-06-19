// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

/** the purpose of the class is filter lidar points that are
 * 1) NOT already in the static obstacle map
 * 2) NOT floor */
public class UnknownObstaclePredicate implements SpacialObstaclePredicate {
  private static final int NON_BLACK_MASK = 0xff00;
  private static final Tensor LIDAR = SensorsConfig.GLOBAL.vlp16Gokart();
  // ---
  private final SpacialObstaclePredicate floorPredicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final PredefinedMap predefinedMap;
  private GeometricLayer geometricLayer;

  public UnknownObstaclePredicate() {
    predefinedMap = LocalizationConfig.getPredefinedMapObstacles();
    geometricLayer = GeometricLayer.of(predefinedMap.getModel2Pixel());
  }

  /** since the obstacle query uses a predefined map of the terrain,
   * before calling {@link #isObstacle(Tensor)} with lidar points in local
   * coordinates, the pose estimate has to be set.
   * 
   * @param xya */
  public void setPose(Tensor xya) {
    geometricLayer = GeometricLayer.of(predefinedMap.getModel2Pixel());
    geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(xya));
    geometricLayer.pushMatrix(LIDAR);
  }

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(Tensor point) {
    Point2D point2d = geometricLayer.toPoint2D(point);
    int rgb = predefinedMap.getRGB(point2d);
    if ((rgb & NON_BLACK_MASK) == NON_BLACK_MASK)
      return false;
    return floorPredicate.isObstacle(point);
  }
}
