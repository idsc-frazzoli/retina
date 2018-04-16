// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.DubendorfHangarLog;
import ch.ethz.idsc.gokart.offline.tab.ComputerSensorsTable;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum ComputerSensorsAnalysis {
  ;
  private static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    ComputerSensorsTable computerSensorsTable = new ComputerSensorsTable();
    File file = DubendorfHangarLog._20180412T114245_7e5b46c2.file(LOG_ROOT);
    OfflineLogPlayer.process(file, computerSensorsTable);
    Export.of(UserHome.file("computersensors.csv"), computerSensorsTable.getTable().map(CsvFormat.strict()));
  }
}
