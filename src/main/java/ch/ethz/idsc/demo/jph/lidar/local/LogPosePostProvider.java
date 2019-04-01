// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.LogPosePostInject;
import ch.ethz.idsc.tensor.Scalar;

public class LogPosePostProvider {
  public static void main(String[] args) throws Exception {
    File root = new File("/media/datahaki/data/gokart/cuts/20190311");
    File dest = new File("/media/datahaki/data/gokart/localization/20190311");
    dest.mkdir();
    List<File> list = Stream.of(root.listFiles()) //
        .filter(File::isDirectory) //
        .sorted() //
        .skip(1) //
        .limit(1) //
        .collect(Collectors.toList());
    for (File folder : list) {
      System.out.println(folder.getName());
      GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder, "log.lcm");
      LidarLocalizationTable lidarLocalizationTable = new LidarLocalizationTable();
      lidarLocalizationTable.listeners.add(new GokartPoseListener() {
        @Override
        public void getEvent(GokartPoseEvent gokartPoseEvent) {
          Scalar quality = gokartPoseEvent.getQuality();
          if (!LocalizationConfig.GLOBAL.isQualityOk(quality))
            System.err.println("quality! " + quality);
        }
      });
      lidarLocalizationTable.lidarLocalizationCore.resetPose(gokartLogInterface.pose());
      // ---
      LogPosePostInject logPosePostInject = new LogPosePostInject();
      lidarLocalizationTable.listeners.add(logPosePostInject);
      logPosePostInject.process(gokartLogInterface.file(), new File(folder, "post.lcm"), lidarLocalizationTable);
    }
  }
}
