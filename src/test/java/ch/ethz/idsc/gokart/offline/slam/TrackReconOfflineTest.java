// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.map.TrackReconManagement;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.cache.CachedLog;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TrackReconOfflineTest extends TestCase {
  public void testSimple() throws IOException {
    File DIRECTORY = HomeDirectory.Pictures("20190701T174152_00");
    DIRECTORY.mkdir();
    MappingConfig mappingConfig = new MappingConfig();
    mappingConfig.obsRadius = Quantity.of(0.8, SI.METER);
    mappingConfig.lBounds = Tensors.vector(20, 20);
    mappingConfig.range = Tensors.vector(40, 40);
    File file = CachedLog._20190701T174152_00.file();
    TrackReconOffline trackReconOffline = new TrackReconOffline(mappingConfig, Quantity.of(0.5, SI.SECOND)) {
      @Override
      public void accept(BufferedImage bufferedImage) {
        // ---
      }
    };
    TrackReconManagement trackReconManagement = trackReconOffline.trackReconManagement;
    assertNull(trackReconManagement.getTrackData());
    assertFalse(trackReconManagement.isClosedTrack());
    // ---
    OfflineLogPlayer.process(file, trackReconOffline);
    // ---
    Tensor trackData = trackReconManagement.getTrackData();
    assertTrue(trackReconManagement.isClosedTrack());
    List<Integer> list = Dimensions.of(trackData);
    assertTrue(10 < list.get(0));
    assertEquals(list.get(1).intValue(), 3);
  }
}
