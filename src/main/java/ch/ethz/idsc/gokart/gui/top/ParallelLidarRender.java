// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class ParallelLidarRender extends LidarRender {
  private Color obstacleColor = null;
  private SpacialXZObstaclePredicate predicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();

  public ParallelLidarRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  public void setObstacleColor(Color obstacleColor) {
    this.obstacleColor = obstacleColor;
  }

  protected Color color(Tensor point) {
    if (Objects.isNull(obstacleColor))
      return color;
    return predicate.isObstacle(point) ? obstacleColor : color;
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get()));
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    if (Objects.nonNull(_points)) {
      Tensor points = _points;
      for (Tensor x : points) {
        graphics.setColor(color(x));
        Point2D point2D = geometricLayer.toPoint2D(x);
        // System.out.println(point2D);
        graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
      }
    }
    geometricLayer.popMatrix();
  }
}
