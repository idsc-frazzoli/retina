// code by ynager adapted by mh
package ch.ethz.idsc.demo.mh;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOffline;
import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOfflineMH;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.io.PngImageWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;

enum RunMappingAnalysisOffline {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    File file = UserHome.file("TireTrackDriving.lcm");
    //File file = UserHome.file("20181203T135247_70097ce1.lcm.00");
    File folder = UserHome.Pictures("log/mapper");
    folder.mkdirs();
    if (!folder.isDirectory())
      throw new RuntimeException();
    Consumer<BufferedImage> consumer = new PngImageWriter(folder);
    MappingConfig config = new MappingConfig();
    config.obsRadius = Quantity.of(0.3, SI.METER);
    config.alongLine = true;
    OfflineLogPlayer.process(file, new MappingAnalysisOfflineMH(config, consumer));
    System.out.print("Done.");
  }
}
