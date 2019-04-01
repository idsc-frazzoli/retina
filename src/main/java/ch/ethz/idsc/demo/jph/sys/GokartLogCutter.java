// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    GokartLogFile gokartLogFile = GokartLogFile._20190401T101109_411917b6;
    File file = DatahakiLogFileLocator.file(gokartLogFile);
    // file = new File("/media/datahaki/media/ethz/gokart/topic/mapping/20180924T104243_1/log.lcm");
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        new File("/media/datahaki/data/gokart/cuts"), //
        gokartLogFile.getTitle());
  }
}
