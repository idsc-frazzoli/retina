// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ abstract class AbstractLidarMapping implements //
    StartAndStoppable, LidarRayBlockListener, Runnable, GokartPoseListener {
  // TODO JPH check rationale behind constant 10000!
  protected static final int LIDAR_SAMPLES = 10_000;
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  /** implementations are encouraged to test quality of pose before using coordinate */
  protected GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  // ---
  protected final int waitMillis; // TODO JPH obsolete
  protected final SpacialXZObstaclePredicate spacialXZObstaclePredicate;
  // TODO JPH thread cannot be in base class but needs to be in real-time wrapper
  private final Thread thread = new Thread(this);
  protected volatile boolean isLaunched = true;
  // ---
  protected final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  /** points_ferry is null or a matrix with dimension Nx3
   * containing the cross-section of the static geometry
   * with the horizontal plane at height of the lidar */
  protected Tensor points_ferry = null;

  /* package */ AbstractLidarMapping(SpacialXZObstaclePredicate spacialXZObstaclePredicate, int waitMillis) {
    this.spacialXZObstaclePredicate = spacialXZObstaclePredicate;
    this.waitMillis = waitMillis;
  }

  @Override // from StartAndStoppable
  public final void start() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    vlp16LcmHandler.startSubscriptions();
    thread.start();
  }

  @Override // from StartAndStoppable
  public final void stop() {
    isLaunched = false;
    thread.interrupt();
    vlp16LcmHandler.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
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
