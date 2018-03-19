// code by jph
package ch.ethz.idsc.demo.gz;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

enum DavisTallyAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    // TODO gz adapt filename
    File file = UserHome.file("gokart/twist/20180108T165210_4/log.lcm");
    DavisEventTable rimoTable = new DavisEventTable(Quantity.of(0.05, "s"));
    OfflineLogPlayer.process(file, rimoTable);
    Export.of(UserHome.file("davis_tally.csv"), rimoTable.getTable().map(CsvFormat.strict()));
  }
}
