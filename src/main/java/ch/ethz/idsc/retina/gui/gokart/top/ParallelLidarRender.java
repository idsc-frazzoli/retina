// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.gui.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class ParallelLidarRender extends LidarRender {
  public ParallelLidarRender(Supplier<Tensor> supplier) {
    super(supplier);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor matrix = Se2Utils.toSE2Matrix(supplier.get());
    geometricLayer.pushMatrix(matrix);
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    if (Objects.nonNull(_points)) {
      Tensor points = _points;
      graphics.setColor(color);
      // Stopwatch stopwatch = Stopwatch.started();
      // rendering 0.035 [s]
      for (Tensor x : points) {
        Point2D point2D = geometricLayer.toPoint2D(x);
        graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
      }
      // System.out.println(stopwatch.display_seconds());
    }
    geometricLayer.popMatrix();
  }
}
