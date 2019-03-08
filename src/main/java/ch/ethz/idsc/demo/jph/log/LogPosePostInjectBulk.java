// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;

/* package */ enum LogPosePostInjectBulk {
  ;
  public static void main(String[] args) throws Exception {
    final File root = new File(StaticHelper.CUTS, "20190307");
    for (File folder : root.listFiles())
      if (folder.isDirectory()) {
        System.out.println(folder);
        LogPosePostInjectSingle.in(folder);
      }
  }
}
