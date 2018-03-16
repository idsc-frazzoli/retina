// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapterTest;
import ch.ethz.idsc.gokart.offline.slam.LocalizationResult;
import ch.ethz.idsc.gokart.offline.slam.LocalizationResultListener;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.SlamOfflineLocalize;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SlamDunkTest extends TestCase {
  public void testSimple() throws Exception {
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = SensorsConfig.GLOBAL.planarEmulatorVlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    OfflineLocalize offlineLocalize = new SlamOfflineLocalize(GokartLogAdapterTest.SIMPLE.model());
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
    // System.out.println(table);
    // System.out.println(offlineLocalize.getTable().get(Tensor.ALL, 7));
    // assertTrue(offlineLocalize.getTable().get(Tensor.ALL, 7).stream().map(Scalar.class::cast).allMatch(clip::isInside));
  }
}
