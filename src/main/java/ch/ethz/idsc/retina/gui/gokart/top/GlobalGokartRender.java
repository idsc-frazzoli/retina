// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseOdometry;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class GlobalGokartRender implements RenderInterface, LidarRayBlockListener {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final ScalarUnaryOperator TO_METER = QuantityMagnitude.singleton("m");
  // ---
  private final GokartPoseOdometry gokartOdometry;
  Tensor _points = Tensors.empty();

  public GlobalGokartRender(GokartPoseOdometry gokartOdometry) {
    this.gokartOdometry = gokartOdometry;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor state = gokartOdometry.getPose(); // units {x[m], y[m], angle[]}
    Scalar x = TO_METER.apply(state.Get(0));
    Scalar y = TO_METER.apply(state.Get(1));
    Scalar angle = state.Get(2);
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(Tensors.of(x, y, angle)));
    // DRAW GOKART
    {
      graphics.setColor(new Color(192, 192, 192, 64));
      graphics.fill(geometricLayer.toPath2D(VEHICLE_MODEL.footprint()));
    }
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(Tensors.vector(0, 0, -Math.PI / 2)));
    if (Objects.nonNull(_points)) {
      Tensor points = _points;
      graphics.setColor(Color.BLUE);
      // Stopwatch stopwatch = Stopwatch.started();
      // rendering 0.035 [s]
      for (Tensor point : points) {
        Point2D point2D = geometricLayer.toPoint2D(point);
        graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 1, 1);
      }
      // System.out.println(stopwatch.display_seconds());
    }
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
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
        // no filter based on height
        points.append(Tensors.vectorDouble(x, y, z));
      }
      _points = points;
    }
    floatBuffer.position(position);
  }
}
