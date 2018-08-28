// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.slam.GyroOfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.LocalizationResult;
import ch.ethz.idsc.gokart.offline.slam.LocalizationResultListener;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalizeWrap;
import ch.ethz.idsc.gokart.offline.slam.PoseScatterImage;
import ch.ethz.idsc.gokart.offline.slam.ScatterImage;
import ch.ethz.idsc.gokart.offline.slam.WallScatterImage;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

class LidarGyroPoseEstimator implements OfflinePoseEstimator, LocalizationResultListener {
  private final GokartLogInterface gokartLogInterface;
  private final OfflineTableSupplier offlineTableSupplier;
  private LocalizationResult localizationResult;

  public LidarGyroPoseEstimator(GokartLogInterface gokartLogInterface) {
    this.gokartLogInterface = gokartLogInterface;
    PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
    ScatterImage scatterImage = new PoseScatterImage(predefinedMap);
    scatterImage = new WallScatterImage(predefinedMap);
    OfflineLocalize offlineLocalize = new GyroOfflineLocalize(predefinedMap.getImageExtruded(), gokartLogInterface.pose(), scatterImage);
    // TODO using the wrap here is an overkill because a table is collected!
    offlineTableSupplier = new OfflineLocalizeWrap(offlineLocalize);
    offlineLocalize.addListener(this);
  }

  @Override // from OfflinePoseEstimator
  public GokartPoseEvent getGokartPoseEvent() {
    return Objects.isNull(localizationResult) //
        ? GokartPoseEvents.getPoseEvent( //
            gokartLogInterface.pose(), //
            RealScalar.ONE)
        : GokartPoseEvents.getPoseEvent( //
            GokartPoseHelper.attachUnits(localizationResult.pose_xyt), //
            localizationResult.ratio);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    offlineTableSupplier.event(time, channel, byteBuffer);
  }

  @Override // from LocalizationResultListener
  public void localizationCallback(LocalizationResult localizationResult) {
    this.localizationResult = localizationResult;
  }
}
