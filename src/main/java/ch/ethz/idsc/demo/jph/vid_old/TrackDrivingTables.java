// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.tab.BasicTrackReplayTable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum TrackDrivingTables {
  ;
  public static final File SINGLETON = //
      new File("/media/datahaki/data/gokart/cuts/20190311/20190311T173809_02/log.lcm");
  // new File("/media/datahaki/data/gokart/cuts/20190311/20190311T173809_01/log.lcm");

  private static void single(File file, File dest_folder, String title) throws IOException {
    OfflineTableSupplier offlineTableSupplier = new BasicTrackReplayTable();
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Tensor table = offlineTableSupplier.getTable();
    Export.of( //
        new File(dest_folder, title + ".csv"), //
        table.map(CsvFormat.strict()));
  }

  static void runSingle() throws IOException {
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(SINGLETON.getParentFile(), SINGLETON.getName());
    File dest_folder = HomeDirectory.file("track_putty");
    dest_folder.mkdir();
    single(gokartLogInterface.file(), dest_folder, //
        gokartLogInterface.file().getParentFile().getName());
  }

  public static void main(String[] args) throws IOException {
    // File folder = HomeDirectory.file("laps");
    // File dest_folder = HomeDirectory.file("track_putty");
    // dest_folder.mkdir();
    // for (File file : folder.listFiles())
    // single(file, dest_folder, file.getName());
    runSingle();
  }
}
