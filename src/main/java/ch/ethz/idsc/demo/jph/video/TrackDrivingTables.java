// code by jph
package ch.ethz.idsc.demo.jph.video;

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
  public static final File SINGLETON = new File("/media/datahaki/data/gokart/cuts/20190329/20190329T144049_03/log.lcm");

  private static void single(File file, File dest_folder) throws IOException {
    String title = file.getParentFile().getName();
    OfflineTableSupplier offlineTableSupplier = new BasicTrackReplayTable();
    OfflineLogPlayer.process(file, offlineTableSupplier);
    Tensor table = offlineTableSupplier.getTable();
    Export.of( //
        new File(dest_folder, title + ".csv"), //
        table.map(CsvFormat.strict()));
  }

  public static void main(String[] args) throws IOException {
    // File folder = new File("/home/datahaki/track_putty/source");
    File folder = SINGLETON.getParentFile();
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder, folder.getName());
    File dest_folder = HomeDirectory.file("track_putty");
    dest_folder.mkdir();
    // for (File file : folder.listFiles())
    single(gokartLogInterface.file(), dest_folder);
  }
}
