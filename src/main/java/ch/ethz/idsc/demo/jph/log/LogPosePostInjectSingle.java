// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.LidarGyroPoseEstimator;
import ch.ethz.idsc.gokart.offline.pose.LogPosePostInject;
import ch.ethz.idsc.gokart.offline.slam.VoidScatterImage;

/* package */ enum LogPosePostInjectSingle {
  ;
  public static void in(File folder) throws Exception {
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    final File post_lcm = new File(folder, StaticHelper.POST_LCM);
    if (post_lcm.isFile()) {
      // System.err.println("delete " + target);
      // target.delete();
      // System.out.println("skip " + folder);
    } else {
      LidarGyroPoseEstimator lidarGyroPoseEstimator = //
          new LidarGyroPoseEstimator(gokartLogInterface, VoidScatterImage.INSTANCE);
      LogPosePostInject logPosePostInject = new LogPosePostInject();
      lidarGyroPoseEstimator.offlineLocalize.addListener(logPosePostInject);
      logPosePostInject.process(gokartLogInterface.file(), post_lcm, lidarGyroPoseEstimator);
    }
    if (post_lcm.isFile()) {
      final File source = new File(folder, StaticHelper.LOG_LCM);
      if (source.isFile() && source.length() <= post_lcm.length()) {
        source.delete();
        System.out.println("remove " + source);
      }
    }
  }
}
