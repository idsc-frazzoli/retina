// code by jph
package ch.ethz.idsc.demo.jph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.gui.top.StoreMapUtil;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.slam.GyroOfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalizeWrap;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum SlamComparison {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    BufferedImage map_image = StoreMapUtil.loadExtrude(3);
    for (File folder : OfflineIndex.folders(UserHome.file("gokart/LocalQuick"))) {
      System.out.println(folder);
      GokartLogInterface olr = GokartLogAdapter.of(folder);
      // ---
      OfflineLocalize offlineLocalize = new GyroOfflineLocalize(olr.model());
      offlineLocalize.setScoreImage(map_image);
      OfflineTableSupplier offlineTableSupplier = new OfflineLocalizeWrap(offlineLocalize);
      OfflineLogPlayer.process(olr.file(), offlineTableSupplier);
      Export.of(UserHome.file(folder.getName() + "_gyro.csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
      offlineLocalize.end();
    }
  }
}
