// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.offline.tab.ComputerSensorsTable;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

/** Failure: the measurements of computer sensors blocks the operation
 * of the other modules. therefore no post processing is available. */
enum ComputerSensorsAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    ComputerSensorsTable computerSensorsTable = new ComputerSensorsTable();
    File file = DatahakiLogFileLocator.file(GokartLogFile._20180412T114245_7e5b46c2);
    OfflineLogPlayer.process(file, computerSensorsTable);
    Export.of(UserHome.file("computersensors.csv"), computerSensorsTable.getTable().map(CsvFormat.strict()));
  }
}
