// code by jph
package ch.ethz.idsc.demo.mh;

import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.tab.DavisImuTable;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

/** export of davis240c imu content to determine accuracy of measurements.
 * subsequently, the gyro readings are used to stabilize the lidar based
 * localization algorithm.
 * 
 * https://github.com/idsc-frazzoli/retina/files/1801712/20180131_davis_imu.pdf */
enum GyroAnalysis {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // for (File folder : OfflineIndex.folders(UserHome.file("gokart/LocalQuick"))) {
    // System.out.println(folder);
    // GokartLogInterface olr = GokartLogAdapter.of(folder);
    // ---
    DavisImuTable davisImuTable = new DavisImuTable(Quantity.of(1, SI.SECOND));
    OfflineLogPlayer.process(UserHome.file("20180611T095800_851c404d.lcm.00"), davisImuTable);
    Export.of(UserHome.file("output.csv"), davisImuTable.getTable().map(CsvFormat.strict()));
    // }
  }
}
