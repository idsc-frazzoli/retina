// code by ynager
package ch.ethz.idsc.demo.jph.lidar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOffline;
import ch.ethz.idsc.retina.util.io.PngImageWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum MappingAnalysisImages {
  ;
  public static void main(String[] args) throws Exception {
    File file = new File("/media/datahaki/media/ethz/gokart/topic/mapping/20180827T155655_1/log.lcm");
    File folder = HomeDirectory.Pictures("log/mapping");
    folder.mkdirs();
    Consumer<BufferedImage> consumer = new PngImageWriter(folder);
    OfflineLogPlayer.process(file, new MappingAnalysisOffline(MappingConfig.GLOBAL, consumer));
    System.out.print("Done.");
  }
}
