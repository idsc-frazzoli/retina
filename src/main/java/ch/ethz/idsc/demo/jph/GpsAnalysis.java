// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.tab.LocalizationTable;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

enum GpsAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    for (File folder : OfflineIndex.folders(UserHome.file("gokart/GpsAnalysis"))) {
      System.out.println(folder);
      GokartLogInterface olr = GokartLogAdapter.of(folder);
      // ---
      LocalizationTable localizationAnalysis = new LocalizationTable(Quantity.of(0.5, SI.SECOND), false);
      OfflineLogPlayer.process(olr.file(), localizationAnalysis);
      Export.of(UserHome.file(folder.getName() + ".csv"), localizationAnalysis.getTable().map(CsvFormat.strict()));
    }
  }
}
