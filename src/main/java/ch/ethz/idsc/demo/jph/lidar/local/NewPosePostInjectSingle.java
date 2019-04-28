// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.LidarLocalizationOffline;
import ch.ethz.idsc.gokart.offline.pose.LogPosePostInject;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum NewPosePostInjectSingle {
  ;
  public static void in(GokartLogInterface gokartLogInterface) throws Exception {
    in(gokartLogInterface.file(), gokartLogInterface.pose());
  }

  public static void in(File origin, Tensor pose) throws Exception {
    final File post_lcm = HomeDirectory.file(StaticHelper.POST_LCM);
    LidarLocalizationOffline lidarLocalizationOffline = new LidarLocalizationOffline(pose);
    LogPosePostInject logPosePostInject = new LogPosePostInject();
    lidarLocalizationOffline.gokartPoseListeners.add(logPosePostInject);
    logPosePostInject.process(origin, post_lcm, lidarLocalizationOffline);
  }

  public static void main(String[] args) throws Exception {
    // GokartLogInterface gokartLogInterface = GokartLogAdapter.of(new File("/media/datahaki/data/gokart/cuts/20190328/20190328T164433_01"));
    in( //
        DatahakiLogFileLocator.file(GokartLogFile._20190418T145229_b6a70baf), //
        Tensors.fromString("{28.9864287[m], 27.5183134[m], -1.1886032}"));
  }
}
