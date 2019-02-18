// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.slam.LidarGyroOfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalizeWrap;
import ch.ethz.idsc.gokart.offline.slam.ScatterImage;
import ch.ethz.idsc.tensor.Scalar;

public class LidarGyroPoseEstimator implements OfflineLogListener {
  public final OfflineLocalize offlineLocalize;
  private final OfflineTableSupplier offlineTableSupplier;

  public LidarGyroPoseEstimator(GokartLogInterface gokartLogInterface, ScatterImage scatterImage) {
    PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
    offlineLocalize = new LidarGyroOfflineLocalize(predefinedMap.getImageExtruded(), gokartLogInterface.pose(), scatterImage);
    // TODO using the wrap here is an overkill because a table is collected!
    offlineTableSupplier = new OfflineLocalizeWrap(offlineLocalize);
  }

  @Override // from OfflineLogListener
  public void event(long utime, Scalar time, String channel, ByteBuffer byteBuffer) {
    offlineTableSupplier.event(utime, time, channel, byteBuffer);
  }
}
