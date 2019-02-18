// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
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
import junit.framework.TestCase;

/** the test matches 3 consecutive lidar scans to the dubendorf hangar map
 * the matching qualities are 51255, 43605, 44115 */
public class SlamDunkTest extends TestCase {
  private static void _checkSimple(LidarSpacialProvider lidarSpacialProvider) throws Exception {
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
    ScatterImage scatterImage = new PoseScatterImage(predefinedMap);
    OfflineLocalize offlineLocalize = new SlamOfflineLocalize(predefinedMap.getImageExtruded(), GokartLogAdapterTest.SIMPLE.pose(), scatterImage);
    TableBuilder tb = new TableBuilder();
    LocalizationResultListener lrl = new LocalizationResultListener() {
      @Override
      public void localizationCallback(LocalizationResult localizationResult) {
        tb.appendRow(localizationResult.ratio);
      }
    };
    offlineLocalize.addListener(lrl);
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
    Clip clip = Clip.function(0.35, 1);
    Tensor table = tb.toTable();
    assertEquals(table.map(clip), table);
    System.out.println(table);
    // System.out.println(offlineLocalize.getTable().get(Tensor.ALL, 7));
    // assertTrue(offlineLocalize.getTable().get(Tensor.ALL, 7).stream().map(Scalar.class::cast).allMatch(clip::isInside));
  }

  public void testSimple() throws Exception {
    _checkSimple(LocalizationConfig.GLOBAL.planarEmulatorVlp16());
  }

  public void testBlub() throws Exception {
    double angle_offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    LidarSpacialProvider lidarSpacialProvider = //
        VelodynePlanarEmulator.vlp16_p01deg(angle_offset);
    _checkSimple(lidarSpacialProvider);
  }
}
