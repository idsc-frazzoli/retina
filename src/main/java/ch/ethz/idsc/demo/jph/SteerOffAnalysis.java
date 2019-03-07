// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;

import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.tab.PowerSteerTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/** starting on 20180607 the steering system exhibited behavior out of the nominal range
 * 
 * https://github.com/idsc-frazzoli/retina/files/2108415/20180616_power_steering_breakdown.pdf */
/* package */ enum SteerOffAnalysis {
  ;
  private static final File DIRECTORY = HomeDirectory.file("powersteer");

  public static void main(String[] args) {
    DIRECTORY.mkdir();
    for (LogFile logFile : DatahakiLogFileLocator.all()) {
      final File file = new File(DIRECTORY, logFile.getTitle() + ".csv");
      if (file.isFile()) {
        // ---
      } else {
        System.out.println(file);
        try {
          PowerSteerTable powerSteerTable = new PowerSteerTable(Quantity.of(0.05, SI.SECOND));
          OfflineLogPlayer.process(DatahakiLogFileLocator.file(logFile), powerSteerTable);
          Tensor tensor = powerSteerTable.getTable();
          System.out.println(Dimensions.of(tensor));
          Export.of(file, powerSteerTable.getTable().map(CsvFormat.strict()));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
  }
}
