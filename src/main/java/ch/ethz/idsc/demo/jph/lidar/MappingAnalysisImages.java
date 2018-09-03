// code by ynager
package ch.ethz.idsc.demo.jph.lidar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOffline;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.io.PngImageWriter;

enum MappingAnalysisImages {
  ;
  public static void main(String[] args) throws Exception {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    File file = new File("/media/datahaki/media/ethz/gokart/topic/mapping/20180827T155655_1/log.lcm");
    File folder = UserHome.Pictures("log/mapping");
    folder.mkdirs();
    Consumer<BufferedImage> consumer = new PngImageWriter(folder);
    OfflineLogPlayer.process(file, new MappingAnalysisOffline(MappingConfig.GLOBAL, consumer));
    System.out.print("Done.");
  }
}
