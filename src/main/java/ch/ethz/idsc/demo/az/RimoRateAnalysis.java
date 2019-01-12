// code by jph
package ch.ethz.idsc.demo.az;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.tab.RimoRateJoystickTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum RimoRateAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    // _20180307T150715_28f09b86 ok but needs extraction
    // _20180307T151633_28f09b86 no extraction needed
    // _20180307T154859_0cd18c6b no extraction needed (with stops)
    // File file = DatahakiLogFileLocator.file(GokartLogFile._20180307T154859_0cd18c6b);
    // file = UserHome.file("gokart/pursuit/20180307T154859/log.lcm");
    // file = UserHome.file("datasets/gokart_logs/20180423T181849_633cc6e6.lcm.00");
    RimoRateJoystickTable rimoRateTable = new RimoRateJoystickTable(Quantity.of(0.01, SI.SECOND), ByteOrder.BIG_ENDIAN);
    LogFile logFile = GokartLogFile._20180427T121545_22662115;
    File file = AleLogFileLocator.file(logFile);
    OfflineLogPlayer.process(file, rimoRateTable);
    Export.of(HomeDirectory.file(logFile.getTitle() + ".csv"), rimoRateTable.getTable().map(CsvFormat.strict()));
  }
}
