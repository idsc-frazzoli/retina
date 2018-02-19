// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.BrakeDistanceListener;
import ch.ethz.idsc.gokart.offline.OfflineLocalizeAdapter;
import ch.ethz.idsc.gokart.offline.OfflineLocalizeInterface;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum BrakeDistanceAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    File dir = new File("/home/datahaki/gokart/localquick");
    for (File folder : dir.listFiles())
      if (folder.isDirectory()) {
        System.out.println(folder);
        OfflineLocalizeInterface olr = new OfflineLocalizeAdapter(folder);
        // ---
        BrakeDistanceListener brakeDistanceAnalysis = new BrakeDistanceListener(olr);
        OfflineLogPlayer.process(olr.file(), brakeDistanceAnalysis);
        Export.of(UserHome.file(folder.getName() + ".csv"), brakeDistanceAnalysis.getTable().map(CsvFormat.strict()));
      }
  }
}
