// code by ynager
package ch.ethz.idsc.demo.yn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.slam.PlannerAnalysisOffline;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunPlannerAnalysisOffline {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    File file = HomeDirectory.file("gokart", "logs");
    System.out.println(file.getName());
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(file);
    OfflineLogListener oll = new PlannerAnalysisOffline();
    OfflineLogPlayer.process(gokartLogInterface.file(), oll);
    System.out.print("Done.");
  }
}
