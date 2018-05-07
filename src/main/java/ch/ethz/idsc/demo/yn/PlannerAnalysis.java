// code by ynager
package ch.ethz.idsc.demo.yn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.TrajectoryTable;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum PlannerAnalysis {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
    File file = UserHome.file("gokart/logs");
    System.out.println(file.getName());
    GokartLogInterface olr = GokartLogAdapter.of(file);
    // ---
    // ScatterImage scatterImage = new PoseScatterImage(predefinedMap);
    // scatterImage = new WallScatterImage(predefinedMap);
    // ---
    OfflineTableSupplier offlineTableSupplier = new TrajectoryTable();
    OfflineLogPlayer.process(olr.file(), offlineTableSupplier);
    Export.of(UserHome.file(file.getName() + "_planer.csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
    // ImageIO.write(scatterImage.getImage(), "png", UserHome.Pictures(file.getName() + ".png"));
    System.out.print("done.");
  }
}