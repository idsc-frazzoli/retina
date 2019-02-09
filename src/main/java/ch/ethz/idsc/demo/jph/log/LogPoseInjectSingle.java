// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.pose.GokartPosePostChannel;
import ch.ethz.idsc.gokart.offline.pose.LidarGyroPoseEstimator;
import ch.ethz.idsc.gokart.offline.pose.LogPoseInject;
import ch.ethz.idsc.gokart.offline.slam.VoidScatterImage;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

/* package */ enum LogPoseInjectSingle {
  ;
  private static final String FILENAME = "post.lcm";

  public static void post(File folder) throws Exception {
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    File target = new File(folder, FILENAME);
    if (target.isFile()) {
      target.delete();
      // System.out.println("skip " + folder);
    }
    // else
    {
      LidarGyroPoseEstimator lidarGyroPoseEstimator = //
          new LidarGyroPoseEstimator(gokartLogInterface, VoidScatterImage.INSTANCE);
      LogPoseInject logPoseInject = new LogPoseInject();
      lidarGyroPoseEstimator.offlineLocalize.addListener(logPoseInject);
      logPoseInject.process( //
          gokartLogInterface.file(), //
          new File(folder, "post.lcm"), //
          lidarGyroPoseEstimator);
    }
  }

  public static void main(String[] args) throws Exception {
    File folder = new File("/media/datahaki/data/gokart/cuts/20190204/20190204T185052_01");
    post(folder);
    OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(GokartPosePostChannel.INSTANCE);
    File post = new File(folder, FILENAME);
    OfflineLogPlayer.process(post, offlineTableSupplier);
    Export.of(new File(folder, GokartPosePostChannel.INSTANCE.channel() + ".csv"), offlineTableSupplier.getTable().map(CsvFormat.strict()));
  }
}
