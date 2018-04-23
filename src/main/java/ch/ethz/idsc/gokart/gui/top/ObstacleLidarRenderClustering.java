// code by jph,vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.demo.vc.ElkiTest;
import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.perc.SpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class ObstacleLidarRenderClustering extends LidarRender {
  public ObstacleLidarRenderClustering(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
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
      Tensor p = Tensors.empty();
      graphics.setColor(color);
      SpacialObstaclePredicate spacialObstaclePredicate = SimpleSpacialObstaclePredicate.createVlp16();
      for (Tensor point : points) {
        if (spacialObstaclePredicate.isObstacle(point)) {
          Point2D point2D = geometricLayer.toPoint2D(point);
          p.append(Tensors.of(DoubleScalar.of(point2D.getX()), DoubleScalar.of(point2D.getY())));
          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
        }
      }
      System.out.println("Size of p:"+ p.length());
      ElkiTest.testDBSCANResults(p);
    }
    geometricLayer.popMatrix();
  }
}
