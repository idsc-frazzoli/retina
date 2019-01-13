// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.tab.BrakeDistanceTable;
import ch.ethz.idsc.gokart.offline.tab.RimoRateTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/** Post processing to determine emergency braking distance.
 * 
 * https://github.com/idsc-frazzoli/retina/files/1801717/20180217_emergency_braking.pdf
 * 
 * The analysis led to the development of the lidar based emergency braking logic
 * https://www.youtube.com/watch?v=b_Sqy2TmKIk */
enum BrakeDistanceAnalysis {
  ;
  static void brakeAnalysis() throws FileNotFoundException, IOException {
    for (File folder : OfflineIndex.folders(HomeDirectory.file("gokart/BrakeDistanceAnalysis"))) {
      System.out.println(folder);
      GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
      // ---
      BrakeDistanceTable brakeDistanceAnalysis = new BrakeDistanceTable(gokartLogInterface);
      OfflineLogPlayer.process(gokartLogInterface.file(), brakeDistanceAnalysis);
      Export.of(HomeDirectory.file(folder.getName() + ".csv"), brakeDistanceAnalysis.getTable().map(CsvFormat.strict()));
    }
  }

  static void rimo() throws IOException {
    RimoRateTable rimoTable = new RimoRateTable(Quantity.of(0.05, SI.SECOND));
    File file = HomeDirectory.file("temp/20180108T165210_manual.lcm");
    OfflineLogPlayer.process(file, rimoTable);
    Export.of(HomeDirectory.file("maxtorque.csv"), rimoTable.getTable().map(CsvFormat.strict()));
  }

  public static void main(String[] args) throws IOException {
    brakeAnalysis();
    // rimo();
  }
}
