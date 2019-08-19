// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.io.File;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Type;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.FirstLogMessage;
import ch.ethz.idsc.gokart.offline.cache.CachedLog;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;
import junit.framework.TestCase;

public class LidarLocalizationOfflineTest extends TestCase {
  public void testCached() throws Exception {
    final String _predefinedMap = LocalizationConfig.GLOBAL.predefinedMap;
    CachedLog cachedLog = CachedLog._20190404T143912_24;
    final String _planarVmu931Type = SensorsConfig.GLOBAL.planarVmu931Type;// = PlanarVmu931Type.ROT90.name();
    SensorsConfig.GLOBAL.planarVmu931Type = PlanarVmu931Type.FLIPPED.name();
    File file = cachedLog.file();
    GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(FirstLogMessage.of(file, GokartPoseChannel.INSTANCE.channel()).get());
    LidarLocalizationOffline lidarLocalizationOffline = new LidarLocalizationOffline(PredefinedMap.DUBILAB_LOCALIZATION_20190314, gokartPoseEvent.getPose());
    Tensor quality = Tensors.empty();
    GokartPoseListener gokartPoseListener = new GokartPoseListener() {
      @Override
      public void getEvent(GokartPoseEvent getEvent) {
        quality.append(getEvent.getQuality());
      }
    };
    lidarLocalizationOffline.gokartPoseListeners.add(gokartPoseListener);
    OfflineLogPlayer.process(file, lidarLocalizationOffline);
    Scalar mean = Mean.of(quality).Get();
    System.out.println("mean=" + mean); // should be at least 0.8080119758282067
    assertTrue(Scalars.lessThan(RealScalar.of(0.8), mean));
    SensorsConfig.GLOBAL.planarVmu931Type = _planarVmu931Type;
    LocalizationConfig.GLOBAL.predefinedMap = _predefinedMap;
  }
}
