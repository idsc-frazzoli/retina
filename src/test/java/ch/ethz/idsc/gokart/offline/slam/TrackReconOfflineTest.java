// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.offline.cache.CachedLog;
import ch.ethz.idsc.retina.util.io.PngImageWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TrackReconOfflineTest extends TestCase {
  public void testSimple() throws IOException {
    File DIRECTORY = HomeDirectory.Pictures("20190701T174152_00");
    DIRECTORY.mkdir();
    Consumer<BufferedImage> consumer = new PngImageWriter(DIRECTORY);
    MappingConfig mappingConfig = new MappingConfig();
    mappingConfig.obsRadius = Quantity.of(0.8, SI.METER);
    File file = CachedLog._20190701T174152_00.file();
    // FIXME JPH
    // OfflineLogPlayer.process(file, new TrackReconOffline(mappingConfig, Quantity.of(0.5, SI.SECOND)) {
    // @Override
    // public void accept(BufferedImage bufferedImage) {
    // consumer.accept(bufferedImage);
    // }
    // });
    System.out.print("Done.");
  }
}
