// code by ynager
package ch.ethz.idsc.demo.jph.lidar;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOffline;
import ch.ethz.idsc.retina.util.io.BGR3ByteAnimationWriter;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum MappingAnalysisVideo {
  ;
  public static void main(String[] args) throws InterruptedException, Exception {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    File file = new File("/media/datahaki/media/ethz/gokart/topic/mapping/20180827T155655_1/log.lcm");
    final int snaps = 20; // fps
    final String filename = HomeDirectory.file("mapping.mp4").toString();
    try (Mp4AnimationWriter mp4 = new Mp4AnimationWriter(filename, new Dimension(640, 640), snaps)) {
      Consumer<BufferedImage> consumer = new BGR3ByteAnimationWriter(mp4);
      OfflineLogPlayer.process(file, new MappingAnalysisOffline(MappingConfig.GLOBAL, Quantity.of(1, SI.SECOND)) {
        @Override
        public void accept(BufferedImage bufferedImage) {
          consumer.accept(bufferedImage);
        }
      });
      System.out.print("Done.");
    }
  }
}
