// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.tab.UniqueTimePoseQualityTable;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum EphemeralUniqueTimePoseQuality {
  ;
  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/media/ethz/gokart/topic/racing4o");
    File target = HomeDirectory.file("Projects/ephemeral/src/main/resources/dubilab/app/pose/4o");
    for (File file : folder.listFiles()) {
      System.out.println(file);
      UniqueTimePoseQualityTable.process(file, target);
    }
    // File file = new File("/media/datahaki/media/ethz/gokart/topic/pedestrian/20180503T160522_1/log.lcm");
    // process(file, UserHome.file("Projects/ephemeral/src/main/resources/dubilab/app/filter/slow"));
  }
}
