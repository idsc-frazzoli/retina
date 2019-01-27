// code by mh
package ch.ethz.idsc.demo.mh;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.TrackReconOffline;
import ch.ethz.idsc.retina.util.io.PngImageWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum RunTrackReconOffline {
  ;
  private static final File DIRECTORY = HomeDirectory.Pictures("log", "mapper");

  public static void main(String[] args) throws FileNotFoundException, IOException {
    DIRECTORY.mkdirs();
    if (!DIRECTORY.isDirectory())
      throw new RuntimeException();
    for (File file : DIRECTORY.listFiles())
      file.delete();
    // ---
    File file = UserName.is("datahaki") //
        ? new File("/media/datahaki/media/ethz/gokart/topic/trackid", "changingtrack.lcm")
        : HomeDirectory.file("changingtrack.lcm");
    // File file = UserHome.file("TireTrackDriving.lcm");
    // File file = UserHome.file("20181203T135247_70097ce1.lcm.00");
    // ---
    if (!file.isFile())
      throw new RuntimeException();
    // ---
    Consumer<BufferedImage> consumer = new PngImageWriter(DIRECTORY);
    MappingConfig mappingConfig = new MappingConfig();
    mappingConfig.obsRadius = Quantity.of(0.8, SI.METER);
    OfflineLogPlayer.process(file, new TrackReconOffline(mappingConfig, consumer));
    System.out.print("Done.");
  }
}
