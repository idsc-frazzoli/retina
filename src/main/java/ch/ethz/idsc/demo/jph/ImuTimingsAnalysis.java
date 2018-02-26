// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
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

enum ImuTimingsAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    for (File folder : OfflineIndex.folders(UserHome.file("gokart/ImuTimings"))) {
      // System.out.println(folder);
      String parent = folder.getParentFile().getName();
      final String name = parent + "_" + folder.getName();
      System.out.println(name);
      GokartLogInterface olr = GokartLogAdapter.of(folder);
      // ---
      DavisImuTable davisImuTable = new DavisImuTable(Quantity.of(0, "s"));
      OfflineLogPlayer.process(olr.file(), davisImuTable);
      Export.of(UserHome.file("csv/" + name + ".csv"), davisImuTable.getTable().map(CsvFormat.strict()));
    }
  }
}
