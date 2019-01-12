// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineIndex;
import ch.ethz.idsc.gokart.offline.tab.DavisImuTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/** export of davis240c imu content to determine accuracy of measurements.
 * subsequently, the gyro readings are used to stabilize the lidar based
 * localization algorithm.
 * 
 * https://github.com/idsc-frazzoli/retina/files/1801712/20180131_davis_imu.pdf */
/* package */ enum GyroAnalysis {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    for (File folder : OfflineIndex.folders(HomeDirectory.file("gokart", "LocalQuick"))) {
      System.out.println(folder);
      GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
      // ---
      DavisImuTable davisImuTable = new DavisImuTable(Quantity.of(0, SI.SECOND));
      OfflineLogPlayer.process(gokartLogInterface.file(), davisImuTable);
      Export.of(HomeDirectory.file(folder.getName() + ".csv"), davisImuTable.getTable().map(CsvFormat.strict()));
    }
  }
}
