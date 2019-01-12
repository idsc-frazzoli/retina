// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.tab.LocalizationTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/** export to determine characteristics and accuracy of gps sensor
 * 
 * https://github.com/idsc-frazzoli/retina/issues/147 */
/* package */ enum GpsAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    for (File folder : OfflineIndex.folders(HomeDirectory.file("gokart/GpsAnalysis"))) {
      System.out.println(folder);
      GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
      // ---
      LocalizationTable localizationAnalysis = new LocalizationTable(Quantity.of(0.5, SI.SECOND), true);
      OfflineLogPlayer.process(gokartLogInterface.file(), localizationAnalysis);
      Export.of(HomeDirectory.file(folder.getName() + ".csv"), localizationAnalysis.getTable().map(CsvFormat.strict()));
    }
  }
}
