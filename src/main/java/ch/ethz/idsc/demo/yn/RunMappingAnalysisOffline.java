// code by ynager
package ch.ethz.idsc.demo.yn;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOffline;
import ch.ethz.idsc.retina.util.io.PngAnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum RunMappingAnalysisOffline {
  ;
  public static void main(String[] args) throws Exception {
    File file = HomeDirectory.file("gokart/logs/20180503/20180503T160522_short.lcm");
    try (AnimationWriter animationWriter = new PngAnimationWriter(HomeDirectory.Pictures("log/mapper"))) {
      OfflineLogPlayer.process(file, new MappingAnalysisOffline(MappingConfig.GLOBAL, Quantity.of(1, SI.SECOND)) {
        @Override
        public void accept(BufferedImage bufferedImage) {
          try {
            animationWriter.append(bufferedImage);
          } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException();
          }
        }
      });
    }
    System.out.print("Done.");
  }
}
