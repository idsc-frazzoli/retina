// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.function.Supplier;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

abstract class LidarRender extends AbstractGokartRender implements LidarRayBlockListener {
  public LidarRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  protected Supplier<Tensor> supplier = () -> Array.zeros(3);
  // ---
  /** points in reference frame of lidar */
  Tensor _points = Tensors.empty();
  Color color = Color.BLACK;
  int pointSize = 1;

  /** @param supplier of a 3 vector {x, y, alpha} that describes the reference of
   * the lidar with respect to the (0, 0, 0) center of rear axle of the gokart. */
  public void setReference(Supplier<Tensor> supplier) {
    this.supplier = supplier;
  }

  public final void setColor(Color color) {
    this.color = color;
  }

  public final void setPointSize(int pointSize) {
    this.pointSize = pointSize;
  }

  @Override // from LidarRayBlockListener
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
        // no filter based on height
        points.append(Tensors.vectorDouble(x, y, z));
      }
      _points = points;
    }
    floatBuffer.position(position);
  }
}
