// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.LinmotGetTable;
import ch.ethz.idsc.gokart.offline.tab.RimoGetTable;
import ch.ethz.idsc.gokart.offline.tab.RimoPutTable;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum ProduceReport {
  ;
  public static void main(String[] args) throws IOException {
    File folder = UserHome.file("gokart/linmot/20180412T164740");
    // ---
    GokartLogInterface gli = GokartLogAdapter.of(folder);
    // ---
    List<OfflineTableSupplier> list = Arrays.asList( //
        new LinmotGetTable(), //
        new RimoPutTable(), //
        new RimoGetTable() //
    );
    OfflineLogPlayer.process(gli.file(), list);
    // ---
    File dir = UserHome.file("export/" + folder.getName() + "/csv");
    dir.mkdirs();
    for (OfflineTableSupplier ots : list) {
      Export.of(new File(dir, ots.getClass().getSimpleName() + ".csv"), ots.getTable().map(CsvFormat.strict()));
    }
  }
}
