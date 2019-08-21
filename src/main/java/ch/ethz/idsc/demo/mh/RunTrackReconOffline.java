// code by mh
package ch.ethz.idsc.demo.mh;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.TrackReconOffline;
import ch.ethz.idsc.retina.util.io.PngAnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum RunTrackReconOffline {
  ;
  private static final File DIRECTORY = HomeDirectory.Pictures("20190701T174152_00");

  public static void main(String[] args) throws Exception {
    DIRECTORY.mkdirs();
    if (!DIRECTORY.isDirectory())
      throw new RuntimeException();
    for (File file : DIRECTORY.listFiles())
      file.delete();
    // ---
    File file;
    file = new File("/media/datahaki/data/gokart/0701map/20190701/20190701T174152_00", "20190701T174152_00.lcm");
    // ---
    if (!file.isFile())
      throw new RuntimeException();
    // ---
    try (AnimationWriter animationWriter = new PngAnimationWriter(DIRECTORY)) {
      MappingConfig mappingConfig = new MappingConfig();
      mappingConfig.obsRadius = Quantity.of(0.8, SI.METER);
      OfflineLogPlayer.process(file, new TrackReconOffline(mappingConfig, Quantity.of(0.2, SI.SECOND)) {
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
    }
    System.out.print("Done.");
  }
}
