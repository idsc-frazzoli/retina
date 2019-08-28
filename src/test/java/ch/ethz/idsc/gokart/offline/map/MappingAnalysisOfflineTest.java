// code by jph
package ch.ethz.idsc.gokart.offline.map;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.cache.CachedLog;
import ch.ethz.idsc.retina.util.io.PngAnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MappingAnalysisOfflineTest extends TestCase {
  public void testSimple() throws Exception {
    try (AnimationWriter animationWriter = new PngAnimationWriter(HomeDirectory.Pictures("20190701T174152_00"))) {
      MappingConfig mappingConfig = new MappingConfig();
      mappingConfig.obsRadius = Quantity.of(0.8, SI.METER);
      File file = CachedLog._20190701T174152_00.file();
      OfflineLogPlayer.process(file, new MappingAnalysisOffline(mappingConfig, Quantity.of(2, SI.SECOND)) {
        @Override
        public void accept(BufferedImage bufferedImage) {
          try {
            animationWriter.write(bufferedImage);
          } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException();
          }
        }
      });
    }
    System.out.print("Done.");
  }
}
