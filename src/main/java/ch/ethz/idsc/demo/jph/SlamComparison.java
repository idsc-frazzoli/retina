// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.slam.GyroOfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalizeWrap;
import ch.ethz.idsc.gokart.offline.slam.PoseScatterImage;
import ch.ethz.idsc.gokart.offline.slam.ScatterImage;
import ch.ethz.idsc.gokart.offline.slam.WallScatterImage;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** aggregation of lidar scans relative to pose into single image
 * 
 * https://github.com/idsc-frazzoli/retina/files/1801718/20180221_2nd_gen_localization.pdf
 * https://github.com/idsc-frazzoli/retina/files/2299868/20180818_datasets_track_w.pdf */
/* package */ enum SlamComparison {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
    for (File folder : OfflineIndex.folders(new File("/media/datahaki/media/ethz/gokart/topic", "track_azure.properties"))) {
      System.out.println(folder);
      GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
      // System.out.println(olr.model());
      // ---
      ScatterImage scatterImage = new PoseScatterImage(predefinedMap);
      scatterImage = new WallScatterImage(predefinedMap);
      OfflineLocalize offlineLocalize = new GyroOfflineLocalize(predefinedMap.getImageExtruded(), gokartLogInterface.pose(), scatterImage);
      OfflineTableSupplier offlineTableSupplier = new OfflineLocalizeWrap(offlineLocalize);
      OfflineLogPlayer.process(gokartLogInterface.file(), offlineTableSupplier);
      Export.of(HomeDirectory.file(folder.getName() + ".csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
      ImageIO.write(scatterImage.getImage(), "png", HomeDirectory.Pictures(folder.getName() + ".png"));
    }
  }
}
