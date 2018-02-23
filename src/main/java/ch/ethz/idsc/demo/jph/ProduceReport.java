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
    File folder = UserHome.file("gokart/braking/20171213T162832_6");
    // ---
    GokartLogInterface gli = GokartLogAdapter.of(folder);
    // ---
    List<OfflineTableSupplier> list = Arrays.asList( //
        new LinmotGetTable(), //
        new RimoPutTable(), new RimoGetTable() //
    );
    OfflineLogPlayer.process(gli.file(), list);
    // ---
    File dir = UserHome.file("export/" + folder.getName() + "/csv");
    dir.mkdirs();
    for (OfflineTableSupplier ots : list) {
      Export.of(new File(dir, ots.getClass().getSimpleName() + ".csv"), ots.getTable().map(CsvFormat.strict()));
    }
    // System.out.println(Dimensions.of(tensor));
    // {
    // SeriesCollection seriesCollection = new SeriesCollection();
    // seriesCollection.setTitle("brake position");
    // Tensor tensor = lht.getTable();
    // {
    // SeriesContainer seriesContainer = seriesCollection.add(tensor.get(Tensor.ALL, 0), tensor.get(Tensor.ALL, 1));
    // seriesContainer.setName("actual [m]");
    // }
    // {
    // SeriesContainer seriesContainer = seriesCollection.add(tensor.get(Tensor.ALL, 0), tensor.get(Tensor.ALL, 2));
    // seriesContainer.setName("demand [m]");
    // }
    // ListPlot.of(seriesCollection, new Dimension(600, 300), UserHome.Pictures("some.png"));
    // }
  }
}
