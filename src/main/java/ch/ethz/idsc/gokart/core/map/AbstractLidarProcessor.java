// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ abstract class AbstractLidarProcessor implements StartAndStoppable, LidarRayBlockListener, Runnable {
  private final Thread thread = new Thread(this);
  protected boolean isLaunched = true;
  // ---
  protected final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  /** points_ferry is null or a matrix with dimension Nx3
   * containing the cross-section of the static geometry
   * with the horizontal plane at height of the lidar */
  protected Tensor points_ferry = null;

  @Override // from StartAndStoppable
  public void start() {
    vlp16LcmHandler.startSubscriptions();
    thread.start();
  }

  @Override // from StartAndStoppable
  public void stop() {
    isLaunched = false;
    thread.interrupt();
    vlp16LcmHandler.stopSubscriptions();
  }

  @Override // from LidarRayBlockListener
  public final void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (lidarRayBlockEvent.dimensions != 3)
      throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
    // ---
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    points_ferry = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    thread.interrupt();
  }
}
