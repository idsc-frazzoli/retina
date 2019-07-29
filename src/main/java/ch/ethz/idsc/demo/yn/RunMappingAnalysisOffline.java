// code by ynager
package ch.ethz.idsc.demo.yn;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOffline;
import ch.ethz.idsc.retina.util.io.PngImageWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum RunMappingAnalysisOffline {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    File file = HomeDirectory.file("gokart/logs/20180503/20180503T160522_short.lcm");
    File folder = HomeDirectory.Pictures("log/mapper");
    folder.mkdirs();
    if (!folder.isDirectory())
      throw new RuntimeException();
    Consumer<BufferedImage> consumer = new PngImageWriter(folder);
    OfflineLogPlayer.process(file, new MappingAnalysisOffline(MappingConfig.GLOBAL, Quantity.of(1, SI.SECOND)) {
      @Override
      public void accept(BufferedImage bufferedImage) {
        consumer.accept(bufferedImage);
      }
    });
    System.out.print("Done.");
  }
}
