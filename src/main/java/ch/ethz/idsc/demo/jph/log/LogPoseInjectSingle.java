// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.LidarGyroPoseEstimator;
import ch.ethz.idsc.gokart.offline.pose.LogPoseInject;

/* package */ enum LogPoseInjectSingle {
  ;
  public static void post(File folder) throws Exception {
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    File target = new File(folder, "post.lcm");
    if (target.isFile())
      System.out.println("skip " + folder);
    else
      new LogPoseInject().process( //
          gokartLogInterface.file(), //
          new File(folder, "post.lcm"), //
          new LidarGyroPoseEstimator(gokartLogInterface));
  }

  public static void main(String[] args) throws Exception {
    post(new File("/media/datahaki/data/gokart/cuts/20190204/20190204T185052_01"));
  }
}
