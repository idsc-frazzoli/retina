// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
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
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum SlamComparison {
  ;
  private static final Tensor LIDAR = SensorsConfig.GLOBAL.vlp16Gokart();

  public static void main(String[] args) throws FileNotFoundException, IOException {
    PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
    for (File folder : OfflineIndex.folders(UserHome.file("gokart/LocalQuick"))) {
      System.out.println(folder);
      GokartLogInterface olr = GokartLogAdapter.of(folder);
      // ---
      ScatterImage scatterImage = new PoseScatterImage(predefinedMap.getImage(), LIDAR);
      scatterImage = new WallScatterImage(predefinedMap.getImage(), LIDAR);
      OfflineLocalize offlineLocalize = new GyroOfflineLocalize(predefinedMap.getImageExtruded(), olr.model(), scatterImage);
      OfflineTableSupplier offlineTableSupplier = new OfflineLocalizeWrap(offlineLocalize);
      OfflineLogPlayer.process(olr.file(), offlineTableSupplier);
      Export.of(UserHome.file(folder.getName() + "_gyro.csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
      ImageIO.write(scatterImage.getImage(), "png", UserHome.Pictures(folder.getName() + ".png"));
    }
  }
}
