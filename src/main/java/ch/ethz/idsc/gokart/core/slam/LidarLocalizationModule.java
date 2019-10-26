// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.io.File;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmClient;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseVelocityInterface;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/** match the most recent lidar scan to static geometry of a pre-recorded map
 * the module runs a separate thread. on a standard pc the matching takes 0.017[s] on average */
public final class LidarLocalizationModule extends AbstractModule implements PoseVelocityInterface {
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final LidarLocalizationCore lidarLocalizationCore = //
      new LidarLocalizationCore(LocalizationConfig.GLOBAL.getPredefinedMap());
  private final Vlp16LcmClient vlp16LcmClient = //
      SensorsConfig.GLOBAL.vlp16LcmClient(lidarLocalizationCore.velodyneDecoder);

  @Override // from AbstractModule
  protected void first() {
    vmu931ImuLcmClient.addListener(lidarLocalizationCore);
    vmu931ImuLcmClient.startSubscriptions();
    // ---
    vlp16LcmClient.startSubscriptions();
    loadPose();
    lidarLocalizationCore.isLaunched = true;
    lidarLocalizationCore.thread.start();
  }

  @Override // from AbstractModule
  protected void last() {
    savePose();
    lidarLocalizationCore.isLaunched = false;
    lidarLocalizationCore.thread.interrupt();
    vlp16LcmClient.stopSubscriptions();
    vmu931ImuLcmClient.stopSubscriptions();
  }

  /** @return */
  public boolean isTracking() {
    return lidarLocalizationCore.isTracking();
  }

  /** @param tracking */
  public void setTracking(boolean tracking) {
    lidarLocalizationCore.setTracking(tracking);
  }

  /** flag snap */
  public void flagSnap() {
    lidarLocalizationCore.flagSnap();
  }

  /***************************************************/
  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return lidarLocalizationCore.getPose();
  }

  @Override // from PoseVelocityInterface
  public Tensor getVelocity() {
    return lidarLocalizationCore.getVelocity();
  }

  @Override // from PoseVelocityInterface
  public Scalar getGyroZ() {
    return lidarLocalizationCore.getGyroZ();
  }

  public GokartPoseEvent createPoseEvent() {
    return lidarLocalizationCore.createPoseEvent();
  }

  /** function called when operator initializes pose
   * 
   * @param pose {x[m], y[m], angle[]} */
  public void resetPose(Tensor pose) {
    lidarLocalizationCore.resetPose(pose);
  }

  /***************************************************/
  /** localization map uses 640 pixels which corresponds to 86[m]
   * localization map uses 720 pixels which corresponds to 96[m] */
  private static final Clip CLIP = Clips.positive(Quantity.of(96, SI.METER));
  private static final File CACHE_LAST = new File("resources/cache/last.pose");

  private void loadPose() {
    if (CACHE_LAST.isFile())
      try {
        Tensor pose = Get.of(CACHE_LAST);
        pose.set(CLIP, 0);
        pose.set(CLIP, 1);
        System.out.println("load pose=" + pose.map(Round._4));
        lidarLocalizationCore.resetPose(pose);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  private void savePose() {
    try {
      Tensor pose = lidarLocalizationCore.getPose();
      System.out.println("save pose=" + pose.map(Round._4));
      Put.of(CACHE_LAST, pose);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
