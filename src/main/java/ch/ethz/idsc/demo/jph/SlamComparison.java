// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.slam.GyroOfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalizeWrap;
import ch.ethz.idsc.gokart.offline.slam.SlamOfflineLocalize;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum SlamComparison {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    for (File folder : OfflineIndex.folders(UserHome.file("gokart/LocalQuick"))) {
      System.out.println(folder);
      GokartLogInterface olr = new GokartLogAdapter(folder);
      // ---
      {
        OfflineLocalize offlineLocalize = new SlamOfflineLocalize(olr.model());
        OfflineTableSupplier offlineTableSupplier = new OfflineLocalizeWrap(offlineLocalize);
        OfflineLogPlayer.process(olr.file(), offlineTableSupplier);
        Export.of(UserHome.file(folder.getName() + "_slam.csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
      }
      {
        OfflineLocalize offlineLocalize = new GyroOfflineLocalize(olr.model());
        OfflineTableSupplier offlineTableSupplier = new OfflineLocalizeWrap(offlineLocalize);
        OfflineLogPlayer.process(olr.file(), offlineTableSupplier);
        Export.of(UserHome.file(folder.getName() + "_gyro.csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
      }
    }
  }
}
