// code by jph
package ch.ethz.idsc.gokart.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.davis.app.DavisQuickComponent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.math.ProjectionMatrix;
import ch.ethz.idsc.retina.util.math.Viewport;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.Hue;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.sca.Clip;

public class DavisLidarComponent extends DavisQuickComponent implements LidarRayBlockListener {
  private static final Scalar NUMERIC_ONE = DoubleScalar.of(1);
  // ---
  private Tensor _points;

  @Override
  public void drawComponent(Graphics2D graphics) {
    super.drawComponent(graphics);
    // ---
    Dimension dimension = jComponent.getSize();
    int width = dimension.width;
    int height = width * 180 / 240;
    Viewport viewport = Viewport.create(width, height);
    Tensor projection = //
        ProjectionMatrix.of(RealScalar.of(1.1), viewport.aspectRatio(), Clip.function(1, 100)).unmodifiable();
    if (Objects.nonNull(_points)) {
      Tensor points = _points;
      Tensor rot1 = Rodrigues.exp(SensorsConfig.GLOBAL.vlp16_davis_w1);
      Tensor rot0 = Rodrigues.exp(SensorsConfig.GLOBAL.vlp16_davis_w0);
      Tensor rot = rot1.dot(rot0);
      for (Tensor x : points) {
        Tensor pw = rot.dot(x.add(SensorsConfig.GLOBAL.vlp16_davis_t)).append(NUMERIC_ONE);
        Tensor proj = projection.dot(pw);
        Optional<Point> optional = viewport.toPixel(proj);
        if (optional.isPresent()) {
          Point point = optional.get();
          double hue = proj.Get(2).number().doubleValue() * 0.3;
          Color color = Hue.of(hue, 1, 1, 1);
          graphics.setColor(color);
          graphics.fillRect(width - point.x, height - point.y, 2, 2);
        }
      }
    }
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    final FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    final int position = floatBuffer.position();
    if (lidarRayBlockEvent.dimensions == 2) {
      _points = Tensors.vector(i -> Tensors.of( //
          DoubleScalar.of(floatBuffer.get()), //
          DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    } else //
    if (lidarRayBlockEvent.dimensions == 3) {
      Tensor points = Tensors.empty();
      while (floatBuffer.hasRemaining()) {
        double x = floatBuffer.get();
        double y = floatBuffer.get();
        double z = floatBuffer.get();
        points.append(Tensors.vectorDouble(x, y, z));
      }
      _points = points;
    }
    floatBuffer.position(position);
  }
}
