// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.LidarGyroPoseEstimator;
import ch.ethz.idsc.gokart.offline.pose.LogPoseInject;
import ch.ethz.idsc.owl.bot.util.UserHome;

enum LogPoseInjectSingle {
  ;
  public static void main(String[] args) throws Exception {
    File folder = new File("/media/datahaki/media/ethz/gokart/topic/davis_extracted_logs/20180927T145943");
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    LogPoseInject.process( //
        gokartLogInterface.file(), //
        UserHome.file("some.lcm"), //
        new LidarGyroPoseEstimator(gokartLogInterface));
  }
}
