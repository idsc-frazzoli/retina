// code by vc, jph
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

/** the purpose of the class is to carry out the math for the simple obstacle
 * check method. */
// the class name is preliminary
public class UnknownObstaclePredicate implements SpacialObstaclePredicate {
  protected static final Tensor LIDAR = Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16).unmodifiable();
  private final SpacialObstaclePredicate createVlp16;
  private PredefinedMap predefinedMap;
  private GeometricLayer gl = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));

  public UnknownObstaclePredicate() {
    createVlp16 = SimpleSpacialObstaclePredicate.createVlp16();
    predefinedMap = PredefinedMap.DUBENDORF_HANGAR_20180423obstacles;
  }

  public void setPose(Tensor xya) {
    gl = new GeometricLayer(ViewLcmFrame.MODEL2PIXEL_INITIAL, Array.zeros(3));
    gl.pushMatrix(GokartPoseHelper.toSE2Matrix(xya));
    gl.pushMatrix(LIDAR);
  }

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(Tensor point) {
    Point2D point2d = gl.toPoint2D(point);
    int rgb = predefinedMap.getImage().getRGB((int) point2d.getX(), (int) point2d.getY());
    if ((rgb & 0xff00) == 0xff00) {
      return false;
    }
    return createVlp16.isObstacle(point);
  }

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(double x, double z) {
    throw new RuntimeException();
  }
}
