// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.ProjectionMatrix;
import ch.ethz.idsc.retina.util.math.Viewport;
import ch.ethz.idsc.sophus.lie.so3.So3Exponential;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.Hue;
import ch.ethz.idsc.tensor.sca.Clips;

class PerspectiveLidarRender extends LidarRender {
  private static final Scalar NUMERIC_ONE = DoubleScalar.of(1);
  // ---
  private final Viewport viewport = Viewport.create(240, 180);
  private final Tensor projection = //
      ProjectionMatrix.of(RealScalar.of(1.1), viewport.aspectRatio(), Clips.interval(1, 100)).unmodifiable();

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(_points)) {
      Tensor points = _points;
      graphics.setColor(color);
      Tensor rot1 = So3Exponential.INSTANCE.exp(SensorsConfig.GLOBAL.vlp16_davis_w1);
      Tensor rot0 = So3Exponential.INSTANCE.exp(SensorsConfig.GLOBAL.vlp16_davis_w0);
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
          graphics.fillRect(point.x, point.y, 2, 2);
        }
      }
    }
  }
}
