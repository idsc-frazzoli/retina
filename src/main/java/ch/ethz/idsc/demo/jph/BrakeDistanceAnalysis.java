// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.tab.BrakeDistanceTable;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum BrakeDistanceAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    for (File folder : OfflineIndex.folders(UserHome.file("gokart/BrakeDistanceAnalysis"))) {
      System.out.println(folder);
      GokartLogInterface olr = new GokartLogAdapter(folder);
      // ---
      BrakeDistanceTable brakeDistanceAnalysis = new BrakeDistanceTable(olr);
      OfflineLogPlayer.process(olr.file(), brakeDistanceAnalysis);
      Export.of(UserHome.file(folder.getName() + ".csv"), brakeDistanceAnalysis.getTable().map(CsvFormat.strict()));
    }
  }
}
