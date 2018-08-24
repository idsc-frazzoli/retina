package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.BasicTrackReplayTable;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum TrackDrivingTables {
  ;
  public static void main(String[] args) throws IOException {
    File folder = UserHome.file("export_red");
    for (File file : folder.listFiles()) {
      String title = file.getName();
      System.out.println(title);
      OfflineTableSupplier offlineTableSupplier = new BasicTrackReplayTable();
      OfflineLogPlayer.process(file, offlineTableSupplier);
      Tensor table = offlineTableSupplier.getTable();
      Export.of(UserHome.file("track_r/" + title + ".csv"), table.map(CsvFormat.strict()));
    }
  }
}
