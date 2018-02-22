// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.tab.DavisImuTable;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

enum GyroAnalysis {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    for (File folder : OfflineIndex.folders(UserHome.file("gokart/LocalQuick"))) {
      System.out.println(folder);
      GokartLogInterface olr = GokartLogAdapter.of(folder);
      // ---
      DavisImuTable davisImuTable = new DavisImuTable(Quantity.of(0, "s"));
      OfflineLogPlayer.process(olr.file(), davisImuTable);
      Export.of(UserHome.file(folder.getName() + ".csv"), davisImuTable.getTable().map(CsvFormat.strict()));
    }
  }
}
