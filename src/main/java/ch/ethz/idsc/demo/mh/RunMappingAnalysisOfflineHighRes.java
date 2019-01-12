// code by ynager adapted by mh
package ch.ethz.idsc.demo.mh;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.io.PngImageWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

enum RunMappingAnalysisOfflineHighRes {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    // File file = UserHome.file("changingtrack.lcm");
    File file = HomeDirectory.file("TireTrackDriving.lcm");
    // File file = UserHome.file("20181203T135247_70097ce1.lcm.00");
    File folder = HomeDirectory.Pictures("log/mapperHR");
    folder.mkdirs();
    if (!folder.isDirectory())
      throw new RuntimeException();
    Consumer<BufferedImage> consumer = new PngImageWriter(folder);
    MappingConfig config = new MappingConfig();
    config.obsRadius = Quantity.of(0.8, SI.METER);
    // MappingConfig.GLOBAL.P_M = RealScalar.of(0.95);
    OfflineLogPlayer.process(file, new MappingAnalysisOfflineHighResMH(config, consumer));
    System.out.print("Done.");
  }
}
