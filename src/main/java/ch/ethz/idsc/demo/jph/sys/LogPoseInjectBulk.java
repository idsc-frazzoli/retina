// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.LidarGyroPoseEstimator;
import ch.ethz.idsc.gokart.offline.pose.LogPoseInject;

enum LogPoseInjectBulk {
  ;
  public static void main(String[] args) throws Exception {
    File root = new File("/media/datahaki/media/ethz/gokart/topic/track_orange");
    File targ = new File("/media/datahaki/media/ethz/gokart/topic/racing4o");
    for (File folder : root.listFiles())
      if (folder.isDirectory()) {
        System.out.println(folder);
        File dst = new File(targ, folder.getName() + ".lcm");
        if (dst.isFile()) {
          System.err.println("file already exists, skipping:");
          System.out.println(dst);
        } else {
          // dst.delete();
          GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
          LogPoseInject.process( //
              gokartLogInterface.file(), dst, new LidarGyroPoseEstimator(gokartLogInterface));
        }
      }
  }
}
