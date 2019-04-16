// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

/* package */ enum RunLidarLocalizationTable {
  ;
  public static void main(String[] args) throws IOException {
    File root = new File("/media/datahaki/data/gokart/cuts/20190311");
    File dest = new File("/media/datahaki/data/gokart/localization/20190311");
    dest.mkdir();
    List<File> list = Stream.of(root.listFiles()) //
        .filter(File::isDirectory) //
        .sorted() //
        .skip(2) //
        .limit(1) //
        .collect(Collectors.toList());
    for (File folder : list) {
      System.out.println(folder.getName());
      GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder, "log.lcm");
      LidarLocalizationOffline lidarLocalizationOffline = new LidarLocalizationOffline(gokartLogInterface.pose());
      lidarLocalizationOffline.gokartPoseListeners.add(new GokartPoseListener() {
        @Override
        public void getEvent(GokartPoseEvent gokartPoseEvent) {
          System.out.println(gokartPoseEvent.getQuality());
        }
      });
      LidarLocalizationTable lidarLocalizationTable = //
          new LidarLocalizationTable(lidarLocalizationOffline.lidarLocalizationCore());
      OfflineLogPlayer.process(gokartLogInterface.file(), lidarLocalizationOffline);
      Export.of(new File(dest, folder.getName() + ".csv.gz"), lidarLocalizationTable.tableBuilder.toTable().map(CsvFormat.strict()));
      Export.of(new File(dest, folder.getName() + "_odom.csv.gz"), lidarLocalizationTable.tableBuilderOdometry.toTable().map(CsvFormat.strict()));
    }
  }
}
