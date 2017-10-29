// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.util.function.Supplier;

import ch.ethz.idsc.owly.gui.GeometricLayer;
import ch.ethz.idsc.owly.gui.RenderInterface;
import ch.ethz.idsc.owly.math.se2.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class LidarRender implements RenderInterface, LidarRayBlockListener {
  private final Supplier<Tensor> supplier;
  private Tensor _points = Tensors.empty();
  private Color color = Color.BLACK;

  public LidarRender(Supplier<Tensor> supplier) {
    this.supplier = supplier;
  }

  public void setColor(Color color) {
    this.color = color;
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
    Tensor points = _points;
    graphics.setColor(color);
    // Stopwatch stopwatch = Stopwatch.started();
    // rendering 0.035 [s]
    for (Tensor x : points) {
      Point2D point2D = geometricLayer.toPoint2D(x);
      graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 1, 1);
    }
    // System.out.println(stopwatch.display_seconds());
    geometricLayer.popMatrix();
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (lidarRayBlockEvent.dimensions == 2) {
      FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
      _points = Tensors.vector(i -> Tensors.of( //
          DoubleScalar.of(floatBuffer.get()), //
          DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    } else //
    if (lidarRayBlockEvent.dimensions == 3) {
      Tensor points = Tensors.empty();
      while (lidarRayBlockEvent.floatBuffer.hasRemaining()) {
        double x = lidarRayBlockEvent.floatBuffer.get();
        double y = lidarRayBlockEvent.floatBuffer.get();
        double z = lidarRayBlockEvent.floatBuffer.get();
        if (z < 0.1)
          points.append(Tensors.vectorDouble(x, y, z));
      }
      _points = points;
    }
  }
}
