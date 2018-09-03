// code by ynager
package ch.ethz.idsc.demo.yn;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOffline;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.io.PngImageWriter;

enum RunMappingAnalysisOffline {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    File file = new File("/home/ynager/gokart/logs/20180503/20180503T160522_short.lcm");
    File folder = UserHome.Pictures("log/mapper");
    folder.mkdirs();
    if (!folder.isDirectory())
      throw new RuntimeException();
    Consumer<BufferedImage> consumer = new PngImageWriter(folder);
    OfflineLogPlayer.process(file, new MappingAnalysisOffline(MappingConfig.GLOBAL, consumer));
    System.out.print("Done.");
  }
}
