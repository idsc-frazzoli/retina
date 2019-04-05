// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;

/* package */ enum LogPosePostInjectBulk {
  ;
  public static void main(String[] args) throws Exception {
    final File root = new File("/media/datahaki/data/gokart/cuts", "20190325");
    for (File folder : root.listFiles())
      if (folder.isDirectory()) {
        System.out.println(folder);
        LogPosePostInjectSingle.in(folder);
      }
  }
}
