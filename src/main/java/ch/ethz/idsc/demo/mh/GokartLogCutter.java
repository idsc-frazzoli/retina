// code by jph
package ch.ethz.idsc.demo.mh;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    // GokartLogFile gokartLogFile = GokartLogFile._20181008T183011_786ab990;
    File file = HomeDirectory.file("changingtrack.lcm");
    // File file = DatahakiLogFileLocator.file(gokartLogFile);
    // file = new File("/media/datahaki/media/ethz/gokart/topic/mapping/20180924T104243_1/log.lcm");
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        HomeDirectory.file("changingtrack"), //
        "changingtrackcut");
  }
}
