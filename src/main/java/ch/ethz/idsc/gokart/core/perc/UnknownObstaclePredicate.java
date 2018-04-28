// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.top.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.gui.top.ViewLcmFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** the purpose of the class is filter lidar points that are
 * 1) NOT already in the static obstacle map
 * 2) NOT floor */
public class UnknownObstaclePredicate implements SpacialObstaclePredicate {
  private static final int NON_BLACK_MASK = 0xff00;
  // TODO definition of LIDAR already exists in the codebase
  protected static final Tensor LIDAR = Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16).unmodifiable();
  // ---
  private final SpacialObstaclePredicate floorPredicate = SimpleSpacialObstaclePredicate.createVlp16();
  private final PredefinedMap predefinedMap;
  private GeometricLayer geometricLayer = //
      new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));

  public UnknownObstaclePredicate() {
    predefinedMap = PredefinedMap.DUBENDORF_HANGAR_20180423OBSTACLES;
  }

  public void setPose(Tensor xya) {
    geometricLayer = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));
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

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(double x, double z) {
    throw new RuntimeException();
  }
}
