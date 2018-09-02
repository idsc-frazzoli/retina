// code by ynager
package ch.ethz.idsc.demo.jph.lidar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.slam.MappingAnalysisOffline;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;

enum RunMappingAnalysisOffline {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    File file = new File("/media/datahaki/data/ethz/export_azure/20180827T170643_6.lcm");
    OfflineLogPlayer.process(file, new MappingAnalysisOffline());
    System.out.print("Done.");
  }
}
