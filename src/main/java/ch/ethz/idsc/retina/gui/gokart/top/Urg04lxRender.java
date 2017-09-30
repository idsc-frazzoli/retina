// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;

import ch.ethz.idsc.owly.gui.GeometricLayer;
import ch.ethz.idsc.owly.gui.RenderInterface;
import ch.ethz.idsc.owly.math.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** renders the obstacles detected by urg04lx front lidar */
class Urg04lxRender implements RenderInterface, LidarRayBlockListener {
  // TODO magic const!
  private static final Tensor OFFSET_X = Se2Utils.toSE2Matrix(Tensors.vector(1.2, 0.0, 0.0));
  // ---
  private Tensor _points = Tensors.empty();

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor points = _points;
    geometricLayer.pushMatrix(OFFSET_X);
    graphics.setColor(Color.GREEN);
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - 2, point2D.getY() - 2, 5, 5));
    }
    graphics.setColor(new Color(128, 0, 0, 128));
    for (Tensor row : points) {
      Point2D point2D = geometricLayer.toPoint2D(row);
      graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 2, 2);
    }
    geometricLayer.popMatrix();
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    _points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
  }
}
