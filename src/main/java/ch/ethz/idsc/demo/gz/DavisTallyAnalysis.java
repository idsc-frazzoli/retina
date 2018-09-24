// code by jph
package ch.ethz.idsc.demo.gz;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

enum DavisTallyAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    // GokartLogFile gokartLogFile = GokartLogFile._20180509T120343_8d5acc24;
    GokartLogFile gokartLogFile = GokartLogFile._20180522T114650_6806b8fd;
    File file = GioeleLogFileLocator.file(gokartLogFile);
    DavisEventTable davisEventTable = //
        // new DavisEventTable(Quantity.of(RationalScalar.of(1, 2), "s"));
        // new DavisEventTable(Quantity.of(RationalScalar.of(1, 4), "s"));
        new DavisEventTable(Quantity.of(RationalScalar.of(1, 50), "s"));
    OfflineLogPlayer.process(file, davisEventTable);
    Export.of( //
        UserHome.file(gokartLogFile.getTitle() + "one.csv"), //
        davisEventTable.getTable().map(CsvFormat.strict()));
  }
}
