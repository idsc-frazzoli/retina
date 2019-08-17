// code by ynager
package ch.ethz.idsc.demo.jph.lidar;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.map.MappingAnalysisOffline;
import ch.ethz.idsc.retina.util.io.PngAnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum MappingAnalysisImages {
  ;
  public static void main(String[] args) throws Exception {
    File file = new File("/media/datahaki/media/ethz/gokart/topic/mapping/20180827T155655_1/log.lcm");
    File folder = HomeDirectory.Pictures("log/mapping");
    folder.mkdirs();
    try (AnimationWriter animationWriter = new PngAnimationWriter(folder)) {
      OfflineLogPlayer.process(file, new MappingAnalysisOffline(MappingConfig.GLOBAL, Quantity.of(1, SI.SECOND)) {
        @Override
        public void accept(BufferedImage bufferedImage) {
          try {
            animationWriter.append(bufferedImage);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      });
    }
    System.out.print("Done.");
  }
}
