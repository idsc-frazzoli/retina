// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

abstract class LidarRender implements RenderInterface, LidarRayBlockListener {
  final Supplier<Tensor> supplier;
  Tensor _points = Tensors.empty();
  Color color = Color.BLACK;
  int pointSize = 1;

  public LidarRender(Supplier<Tensor> supplier) {
    this.supplier = supplier;
  }

  public final void setColor(Color color) {
    this.color = color;
  }

  public final void setPointSize(int pointSize) {
    this.pointSize = pointSize;
  }

  @Override
  public final void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
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
        if (z < 0.1) // height ? TODO magic constant
          points.append(Tensors.vectorDouble(x, y, z));
      }
      _points = points;
    }
    floatBuffer.position(position);
  }
}
