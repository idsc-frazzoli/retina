// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.pose.GokartPosePostChannel;
import ch.ethz.idsc.gokart.offline.pose.LidarGyroPoseEstimator;
import ch.ethz.idsc.gokart.offline.pose.LogPosePostInject;
import ch.ethz.idsc.gokart.offline.slam.VoidScatterImage;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

/* package */ enum LogPosePostInjectSingle {
  ;
  public static void in(File folder) throws Exception {
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    final File target = new File(folder, StaticHelper.FILENAME);
    if (target.isFile()) {
      // System.err.println("delete " + target);
      // target.delete();
      System.out.println("skip " + folder);
    } else {
      LidarGyroPoseEstimator lidarGyroPoseEstimator = //
          new LidarGyroPoseEstimator(gokartLogInterface, VoidScatterImage.INSTANCE);
      LogPosePostInject logPosePostInject = new LogPosePostInject();
      lidarGyroPoseEstimator.offlineLocalize.addListener(logPosePostInject);
      logPosePostInject.process(gokartLogInterface.file(), target, lidarGyroPoseEstimator);
    }
  }

  public static void main(String[] args) throws Exception {
    File folder = new File(StaticHelper.CUTS, "20190204/20190204T185052_01");
    in(folder);
    OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(GokartPosePostChannel.INSTANCE);
    File post = new File(folder, StaticHelper.FILENAME);
    OfflineLogPlayer.process(post, offlineTableSupplier);
    Export.of(new File(folder, GokartPosePostChannel.INSTANCE.channel() + ".csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
  }
}
