// code by ynager
package ch.ethz.idsc.demo.jph.lidar;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.LocalizationMaps;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.map.MappingAnalysisOffline;
import ch.ethz.idsc.retina.util.io.BGR3AnimationWriter;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum MappingAnalysisVideo {
  ;
  public static void main(String[] args) throws InterruptedException, Exception {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    LocalizationConfig localizationConfig = new LocalizationConfig();
    localizationConfig.predefinedMap = LocalizationMaps.DUBILAB_20190314.name();
    File file = new File("/media/datahaki/media/ethz/gokart/topic/mapping/20180827T155655_1/log.lcm");
    final int snaps = 20; // fps
    final String filename = HomeDirectory.file("mapping.mp4").toString();
    Dimension dimension = new Dimension( //
        localizationConfig.getPredefinedMap().getImage().getWidth(), //
        localizationConfig.getPredefinedMap().getImage().getHeight());
    try (AnimationWriter animationWriter = new BGR3AnimationWriter(new Mp4AnimationWriter(filename, dimension, snaps))) {
      OfflineLogPlayer.process(file, new MappingAnalysisOffline(localizationConfig, MappingConfig.GLOBAL, Quantity.of(1, SI.SECOND)) {
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
      System.out.print("Done.");
    }
  }
}
