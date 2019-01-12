// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.BasicTrackReplayTable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum TrackDrivingTables {
  ;
  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/data/ethz/export_azure");
    File dest = HomeDirectory.file("track_azure");
    dest.mkdir();
    for (File file : folder.listFiles()) {
      String title = file.getName();
      System.out.println(title);
      OfflineTableSupplier offlineTableSupplier = new BasicTrackReplayTable();
      OfflineLogPlayer.process(file, offlineTableSupplier);
      Tensor table = offlineTableSupplier.getTable();
      Export.of(new File(dest, title + ".csv"), table.map(CsvFormat.strict()));
    }
  }
}
