// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** the pose server publishes pose values even when the pose
 * is not initialized (in that case the quality == 0) */
public class PoseLcmServerModule extends AbstractClockedModule {
  private static final Scalar PERIOD = Quantity.of(50, SI.PER_SECOND).reciprocal();
  // ---
  private final LidarLocalizationModule lidarLocalizationModule = //
      ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.POSE_LIDAR);

  @Override // from AbstractModule
  protected void first() {
    // ---
  }

  @Override // from AbstractModule
  protected void last() {
    // ---
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    GokartPoseEvent gokartPoseEvent = lidarLocalizationModule.createPoseEvent();
    binaryBlobPublisher.accept(gokartPoseEvent.asArray());
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return PERIOD;
  }
}
