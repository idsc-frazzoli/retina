// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.ObstacleMappingOffline;

/* package */ enum RunObstacleMappingOffline {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    File file;
    file = new File("/media/datahaki/data/gokart/0701map/20190701/20190701T174152_00", "20190701T174152_00.lcm");
    // ---
    if (!file.isFile())
      throw new RuntimeException();
    // ---
    // System.out.println(Pretty.of(PredefinedMap.DUBILAB_LOCALIZATION_20180901.getModel2Pixel()));
    // {{21.564194845968267, 20.85630601628953, -1115.5724258985429}, {20.85630601628953, -21.564194845968267, 335.65181016971474}, {0.0, 0.0, 1.0}}
    OfflineLogPlayer.process(file, new ObstacleMappingOffline());
    System.out.print("Done.");
  }
}
