// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.LidarLocalizationOffline;
import ch.ethz.idsc.gokart.offline.pose.LogPosePostInject;

/* package */ enum NewPosePostInjectSingle {
  ;
  public static void in(File folder) throws Exception {
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    final File post_lcm = new File(folder, StaticHelper.POST_LCM);
    LidarLocalizationOffline lidarLocalizationOffline = new LidarLocalizationOffline(gokartLogInterface.pose());
    LogPosePostInject logPosePostInject = new LogPosePostInject();
    lidarLocalizationOffline.gokartPoseListeners.add(logPosePostInject);
    logPosePostInject.process(gokartLogInterface.file(), post_lcm, lidarLocalizationOffline);
  }

  public static void main(String[] args) throws Exception {
    in(new File("/media/datahaki/data/gokart/cuts/20190328/20190328T164433_01"));
  }
}
