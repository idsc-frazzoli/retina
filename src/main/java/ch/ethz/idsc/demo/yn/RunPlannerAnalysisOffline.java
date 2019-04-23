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
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ enum RunPlannerAnalysisOffline {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    File file = HomeDirectory.file("gokart", "logs");
    System.out.println(file.getName());
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(file);
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    OfflineLogListener offlineLogListener = new PlannerAnalysisOffline(waypoints);
    OfflineLogPlayer.process(gokartLogInterface.file(), offlineLogListener);
    System.out.print("Done.");
  }
}
