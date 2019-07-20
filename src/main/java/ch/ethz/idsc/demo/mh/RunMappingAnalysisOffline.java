// code by ynager adapted by mh
package ch.ethz.idsc.demo.mh;

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

/** generates and exports sequence of B/W images of occupancy grid */
/* package */ enum RunMappingAnalysisOffline {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    File file = HomeDirectory.file("TireTrackDriving.lcm");
    file = new File("/media/datahaki/data/gokart/0719map/20190719/20190719T141611_00", "log.lcm");
    File export = HomeDirectory.Pictures("mapperHR");
    export.mkdirs();
    if (!export.isDirectory())
      throw new RuntimeException();
    Consumer<BufferedImage> consumer = new PngImageWriter(export);
    MappingConfig config = new MappingConfig();
    config.obsRadius = Quantity.of(0.8, SI.METER);
    // MappingConfig.GLOBAL.P_M = RealScalar.of(0.95);
    OfflineLogPlayer.process(file, new MappingAnalysisOffline(config, consumer));
    System.out.print("Done.");
  }
}
