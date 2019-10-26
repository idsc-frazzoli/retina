// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapterTest;
import ch.ethz.idsc.gokart.offline.slam.LocalizationResult;
import ch.ethz.idsc.gokart.offline.slam.LocalizationResultListener;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.PoseScatterImage;
import ch.ethz.idsc.gokart.offline.slam.ScatterImage;
import ch.ethz.idsc.gokart.offline.slam.SlamOfflineLocalize;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

/** the test matches 3 consecutive lidar scans to the dubendorf hangar map
 * the matching qualities are 51255, 43605, 44115 */
public class SlamDunkTest extends TestCase {
  private static void _checkSimple(LocalizationConfig localizationConfig, LidarSpacialProvider lidarSpacialProvider) throws Exception {
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    PredefinedMap predefinedMap = localizationConfig.getPredefinedMap();
    ScatterImage scatterImage = new PoseScatterImage(predefinedMap);
    OfflineLocalize offlineLocalize = new SlamOfflineLocalize(localizationConfig, GokartLogAdapterTest.SIMPLE.pose(), scatterImage);
    TableBuilder tableBuilder = new TableBuilder();
    LocalizationResultListener localizationResultListener = new LocalizationResultListener() {
      @Override
      public void localizationCallback(LocalizationResult localizationResult) {
        tableBuilder.appendRow(localizationResult.quality);
      }
    };
    offlineLocalize.addListener(localizationResultListener);
    lidarAngularFiringCollector.addListener(offlineLocalize);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        offlineLocalize.setTime(time);
        if (channel.equals("vlp16.center.ray"))
          velodyneDecoder.lasers(byteBuffer);
      }
    };
    OfflineLogPlayer.process(GokartLogAdapterTest.SIMPLE.file(), offlineLogListener);
    assertEquals(offlineLocalize.skipped.length(), 1);
    Clip clip = Clips.interval(0.35, 1);
    Tensor table = tableBuilder.getTable();
    System.out.println(table);
    assertEquals(table.map(clip), table);
    // System.out.println(offlineLocalize.getTable().get(Tensor.ALL, 7));
    // assertTrue(offlineLocalize.getTable().get(Tensor.ALL, 7).stream().map(Scalar.class::cast).allMatch(clip::isInside));
  }

  public void testSimple() throws Exception {
    LocalizationConfig localizationConfig = new LocalizationConfig();
    localizationConfig.predefinedMap = LocalizationMaps.DUBILAB_20180901.name();
    _checkSimple(localizationConfig, LocalizationConfig.GLOBAL.planarEmulatorVlp16());
  }

  public void testBlub() throws Exception {
    LocalizationConfig localizationConfig = new LocalizationConfig();
    localizationConfig.predefinedMap = LocalizationMaps.DUBILAB_20180901.name();
    double angle_offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    LidarSpacialProvider lidarSpacialProvider = //
        VelodynePlanarEmulator.vlp16_p01deg(angle_offset);
    _checkSimple(localizationConfig, lidarSpacialProvider);
  }
}
