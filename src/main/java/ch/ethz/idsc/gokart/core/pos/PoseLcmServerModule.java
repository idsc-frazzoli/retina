// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** the pose server publishes pose values even when the pose
 * is not initialized (in that case the quality == 0) */
public class PoseLcmServerModule extends AbstractClockedModule {
  /** rate of publishing pose on lcm */
  public static final Scalar RATE = Quantity.of(50, SI.PER_SECOND);
  // ---
  private final ByteArrayConsumer byteArrayConsumer = new BinaryBlobPublisher(GokartLcmChannel.POSE_LIDAR);
  private final LidarLocalizationModule lidarLocalizationModule = //
      ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);

  @Override // from AbstractModule
  protected void first() {
    Objects.requireNonNull(lidarLocalizationModule);
    /* pre-loads power lookup table so that
     * loading is not required at a later time */
    PowerLookupTable.getInstance();
  }

  @Override // from AbstractModule
  protected void last() {
    // ---
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    byteArrayConsumer.accept(lidarLocalizationModule.createPoseEvent().asArray());
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return RATE.reciprocal();
  }
}
