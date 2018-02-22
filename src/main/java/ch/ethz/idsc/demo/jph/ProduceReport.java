// code by jph
package ch.ethz.idsc.demo.jph;

import java.awt.Dimension;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.tab.LinmotHeatTable;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.plot.ListPlot;
import ch.ethz.idsc.retina.util.plot.SeriesCollection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;

enum ProduceReport {
  ;
  public static void main(String[] args) throws IOException {
    GokartLogInterface gli = GokartLogAdapter.of(UserHome.file("gokart/braking/20171213T162832_6"));
    // ---
    LinmotHeatTable lht = new LinmotHeatTable();
    OfflineLogPlayer.process(gli.file(), lht);
    // ---
    Tensor tensor = lht.getTable();
    System.out.println(Dimensions.of(tensor));
    SeriesCollection sc = new SeriesCollection();
    sc.add(tensor.get(0), tensor.get(1));
    ListPlot.of(sc, new Dimension(600, 300), UserHome.Pictures("some.png"));
  }
}
