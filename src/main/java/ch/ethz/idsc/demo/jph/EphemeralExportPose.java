// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.tab.TimePoseQualityTable;
import ch.ethz.idsc.owl.bot.util.UserHome;

enum EphemeralExportPose {
  ;
  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/media/ethz/gokart/topic/racing4o");
    for (File file : folder.listFiles()) {
      System.out.println(file);
      TimePoseQualityTable.process(file, UserHome.file("Projects/ephemeral/src/main/resources/dubilab/app/pose/4o"));
    }
    // File file = new File("/media/datahaki/media/ethz/gokart/topic/pedestrian/20180503T160522_1/log.lcm");
    // process(file, UserHome.file("Projects/ephemeral/src/main/resources/dubilab/app/filter/slow"));
  }
}
